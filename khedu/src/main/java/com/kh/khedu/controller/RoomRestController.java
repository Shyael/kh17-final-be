package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.MessageDao;
import com.kh.khedu.dao.RoomDao;
import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.room.RoomListResponseVO;
import com.kh.khedu.vo.room.RoomListVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "채팅방 API")

@Slf4j
@RestController
@RequestMapping({"/api/employee/room", "/api/academy/room"})//직원과 학생 모두 여기서 처리
public class RoomRestController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MessageDao messageDao;
	
	@PostMapping("/")
	public void createRoom(//@Valid @RequestBody RoomCreateRequestVO request,
							@CurrentUser TokenParseResponseVO parseVO) {
		int roomNo = roomDao.sequence();
		int roomOwner = parseVO.getAccountNo();
		
		roomDao.insert(RoomDto.builder()
					.roomNo(roomNo)
					.roomOwner(roomOwner)
				.build());
	}
	@GetMapping("/")
	public RoomListResponseVO list(
			//security filter chain에서 permitAll()로 처리된 경우만 null이 가능
			@CurrentUser TokenParseResponseVO parseVO) {
		
		List<RoomListVO> rooms = 
				(StringUtils.hasText(parseVO.getAccountType()) 
						&& (parseVO.getAccountType().contains("학생") 
						|| parseVO.getAccountType().contains("학부모"))) ?
					roomDao.selectList(parseVO.getAccountNo())
					: roomDao.selectList();
		
		return RoomListResponseVO.builder()
					.count(rooms.size())
					.rooms(rooms)
				.build();
	}
	/*
	//방 상세 정보
	@GetMapping("/{roomNo}")
	public RoomDetailResponseVO detail(@PathVariable int roomNo,
						@CurrentUser TokenParseResponseVO parseVO) {
		//방이 있는지 검사 → 404
		RoomDto roomDto = roomDao.selectOne(roomNo);
		if(roomDto == null) throw new TargetNotfoundException();
		
		//참여자 중에 사용자가 존재하는지 검사 → 403
		//List<String> members = roomDao.getMembers(roomNo);//id만
		//if(!members.contains(parseVO.getAccountId())) throw new GetOutException();
		
		List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);//id, 등급, 닉네임
		if(users.stream()
			.map(user->user.getAccountId())
			.noneMatch(accountId->accountId.equals(parseVO.getAccountId())) 
		) {
			throw new GetOutException();
		}
		
		//응답 생성 및 반환
		return RoomDetailResponseVO.builder()
					.room(roomDto)//방정보
					.users(users)//유저목록
				.build();
	}
	
	//방 참여 관련
	@PostMapping("/enter")
	public RoomEnterResponseVO enter(
			@Valid @RequestBody RoomEnterRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomDto roomDto = roomDao.selectOne(request.getRoomNo());
		if(roomDto == null) throw new TargetNotfoundException();
		
		//이미 참여중인지 검사
		List<String> members = roomDao.getMembers(request.getRoomNo());
		if(members.contains(parseVO.getAccountId())) {//이미 참여중이면
			return RoomEnterResponseVO.builder()
						.result(true)
						.message("이미 참여중인 방입니다")
					.build();
		}
		
		//인원제한에 걸려있는지 검사 (null == 무제한)
		if(roomDto.getRoomLimit() != null &&  
				roomDto.getRoomLimit() == members.size()) {
			return RoomEnterResponseVO.builder()
						.result(false)
						.message("해당 방의 정원이 모두 찼습니다")
					.build();
		}
		
		//(+미래) 차단테이블이 따로 있다면 차단테이블을 조회해서 자격 여부를 판정
		//참여 처리
		roomDao.enter(request.getRoomNo(), parseVO.getAccountId());
		LocalDateTime current = LocalDateTime.now();
		
		//메세지 생성
		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
			.content("["+parseVO.getAccountNickname()+"] 님이 입장하셨습니다")
			.level("primary")
			.time(current)
		.build();
		
		//DB저장 처리
		int messageNo = messageDao.sequence();
		messageDao.insertSystem(RoomSystemMessageVO.builder()
					.messageNo(messageNo)
					.messageRoom(request.getRoomNo())
					.messageType(response.getType())
					.messageContent(response.getContent())
					.messageTime(Timestamp.valueOf(response.getTime()))
					.messageLevel(response.getLevel())
				.build());
		
		//*** 중요 ***
		//enter가 발생하고 나서 (DB에 참여처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(request.getRoomNo());
		simpMessagingTemplate.convertAndSend(
			"/public/"+request.getRoomNo()+"/users", users
		);
		//해당 방에 입장 메세지 발송
		simpMessagingTemplate.convertAndSend(
			"/public/"+request.getRoomNo()+"/system", response
		);
		
		//응답 생성 및 반환
		return RoomEnterResponseVO.builder()
					.result(true)
					.message(request.getRoomNo()+"번 채팅방에 입장하셨습니다")
				.build();
	}
	
	//방 나가기 매핑
	@PostMapping("/leave")
	public void leave(@Valid @RequestBody RoomLeaveRequestVO request, 
					@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomDto roomDto = roomDao.selectOne(request.getRoomNo());
		if(roomDto == null) throw new TargetNotfoundException();
		
		//참여중인지 검사는 pass
		
		//참여자 제거
		roomDao.leave(roomDto.getRoomNo(), parseVO.getAccountId());
		
		//시스템메세지를 해당 방으로 발송
		LocalDateTime current = LocalDateTime.now();
		
		//시스템 메세지 준비
		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
					.content("["+parseVO.getAccountNickname()+"] 님이 퇴장하셨습니다")
					.level("primary")
					.time(current)
				.build();
		//시스템메세지를 DB에 저장
		int messageNo = messageDao.sequence();
		messageDao.insertSystem(RoomSystemMessageVO.builder()
					.messageNo(messageNo)
					.messageRoom(request.getRoomNo())
					.messageType(response.getType())
					.messageContent(response.getContent())
					.messageTime(Timestamp.valueOf(response.getTime()))
					.messageLevel(response.getLevel())
				.build());
		//시스템 메세지 발송
		simpMessagingTemplate.convertAndSend(
				"/public/"+roomDto.getRoomNo()+"/system", response
		);
		//*** 중요 ***
		//leave가 발생하고 나서 (DB에 제거처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(request.getRoomNo());
		simpMessagingTemplate.convertAndSend(
			"/public/"+request.getRoomNo()+"/users", users
		);
		
	}
	
	
	//방에서 추방하기 매핑
	@PostMapping("/kick")
	public void kick(@Valid @RequestBody RoomKickRequestVO request, 
					@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomDto roomDto = roomDao.selectOne(request.getRoomNo());
		if(roomDto == null) throw new TargetNotfoundException();
		
		//방장인지 검사
		if(!parseVO.getAccountId().equals(roomDto.getRoomOwner()))
			throw new GetOutException();
		
		//참여자 제거
		roomDao.leave(roomDto.getRoomNo(), request.getAccountId());
		
		//시스템메세지를 해당 방으로 발송
		LocalDateTime current = LocalDateTime.now();
		
		//시스템 메세지 준비
		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
					.content("["+parseVO.getAccountNickname()+"] 님이 추방되셨습니다")
					.level("danger")
					.time(current)
				.build();
		//시스템메세지를 DB에 저장
		int messageNo = messageDao.sequence();
		messageDao.insertSystem(RoomSystemMessageVO.builder()
					.messageNo(messageNo)
					.messageRoom(request.getRoomNo())
					.messageType(response.getType())
					.messageContent(response.getContent())
					.messageTime(Timestamp.valueOf(response.getTime()))
					.messageLevel(response.getLevel())
				.build());
		//(+추가) 추방된 대상이 목록으로 튕겨질 수 있도록 행위를 요청 (action 채널)
		simpMessagingTemplate.convertAndSend(
			"/private/"+roomDto.getRoomNo()+"/action/" + request.getAccountId(),
			"leave"
		);
		//시스템 메세지 발송
		simpMessagingTemplate.convertAndSend(
			"/public/"+roomDto.getRoomNo()+"/system", response
		);
		//*** 중요 ***
		//leave가 발생하고 나서 (DB에 제거처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(request.getRoomNo());
		simpMessagingTemplate.convertAndSend(
			"/public/"+request.getRoomNo()+"/users", users
		);
		
	}
	
	
	//방 메세지 매핑
	@ApiResponse(responseCode = "200", description = "메세지 조회 성공")
	@GetMapping("/{roomNo}/messages")
	public RoomMessagesResponseVO messages(@PathVariable int roomNo,
						@CurrentUser TokenParseResponseVO parseVO) {
		//참여자인지 검사
		List<String> ids = roomDao.getMembers(roomNo);//방 참여자 ID 추출
		if(!ids.contains(parseVO.getAccountId()))
			throw new GetOutException();//403
		
		//메세지 불러오기
		List<MessageVO> messages = messageDao.selectList(roomNo);
		
		//응답 생성
		return RoomMessagesResponseVO.builder()
					.messages(messages)
				.build();
	}
		
	@ApiResponse(responseCode = "200", description = "메세지 조회 성공")
	@PostMapping("/{roomNo}/messages")
	public RoomMessagesResponseVO messages(@PathVariable int roomNo,
				@Valid @RequestBody RoomMessageRequestVO request,
				@CurrentUser TokenParseResponseVO parseVO) {
		//참여자인지 검사
		List<String> ids = roomDao.getMembers(roomNo);//방 참여자 ID 추출
		if(!ids.contains(parseVO.getAccountId()))
			throw new GetOutException();//403
		
		//메세지 불러오기
		//List<MessageVO> messages = messageDao.selectList(roomNo);
		//List<MessageVO> messages = request.getLastMessageNo() == null ?
		//		messageDao.selectList(roomNo, request.getSize())
		//		: messageDao.selectList(roomNo, request.getSize(), request.getLastMessageNo());
		List<MessageVO> messages = messageDao.selectList(roomNo, request);
		int count = messageDao.count(roomNo, request);
		
		//응답 생성
		return RoomMessagesResponseVO.builder()
					.messages(messages)
					.last(messages.size() >= count)
				.build();
	}
	*/
}
