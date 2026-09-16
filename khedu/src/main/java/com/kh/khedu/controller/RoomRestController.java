package com.kh.khedu.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.MessageDao;
import com.kh.khedu.dao.RoomDao;
import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.dto.RoomUserDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.message.MessageVO;
import com.kh.khedu.vo.room.RoomDetailResponseVO;
import com.kh.khedu.vo.room.RoomEnterRequestVO;
import com.kh.khedu.vo.room.RoomEnterResponseVO;
import com.kh.khedu.vo.room.RoomLeaveRequestVO;
import com.kh.khedu.vo.room.RoomListResponseVO;
import com.kh.khedu.vo.room.RoomListVO;
import com.kh.khedu.vo.room.RoomSystemMessageVO;
import com.kh.khedu.vo.room.RoomUserVO;
import com.kh.khedu.vo.room.RoomVO;
import com.kh.khedu.websocket.vo.WebSocketChatVO;
import com.kh.khedu.websocket.vo.WebSocketV4SystemVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
		int roomNo = roomDao.roomSequence();
		int roomOwner = parseVO.getAccountNo();
		
		roomDao.insertRoom(RoomDto.builder()
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
						&& (parseVO.getAccountType().equals("학생") 
						|| parseVO.getAccountType().equals("학부모"))) ?
					roomDao.selectMyList(parseVO.getAccountNo())
					: roomDao.selectConsultList(parseVO.getAccountNo());
		
		return RoomListResponseVO.builder()
					.count(rooms.size())
					.rooms(rooms)
				.build();
	}
	
	//방 상세 정보
	@GetMapping({"/{roomNo}", "/check"})
	public RoomDetailResponseVO detail(@PathVariable(value = "roomNo", required = false) Integer roomNo,
						@CurrentUser TokenParseResponseVO parseVO) {
		//check로 들어왔다면 토큰정보로 방 검색
		if(roomNo == null) {
			int accountNo = parseVO.getAccountNo();
			RoomVO checkVO = roomDao.selectOneForCheck(accountNo);
			//생성된 방이 없다면 생성부터
			if(checkVO == null) {
				RoomDto insertDto = new RoomDto().builder()
							.roomNo(roomDao.roomSequence())
							.roomOwner(accountNo)
							.roomType("상담")
						.build();
				roomDao.insertRoom(insertDto);
				roomDao.insertRoomUser(new RoomUserDto().builder()
							.roomUserNo(roomDao.roomUserSequence())
							.roomNo(insertDto.getRoomNo())
							.accountNo(accountNo)
						.build());
				
				List<RoomUserVO> users = roomDao.getMemberInfo(insertDto.getRoomNo());
				//응답 생성 및 반환
				return RoomDetailResponseVO.builder()
							.room(new RoomVO().builder()
										.roomNo(insertDto.getRoomNo())
										.roomOwner(insertDto.getRoomOwner())
										.roomType(insertDto.getRoomType())
										.roomCtime(null)
									.build())//방정보
							.users(users)//유저목록
							.history(new ArrayList<>())
						.build();
			} 
			else {
				List<RoomUserVO> users = roomDao.getMemberInfo(checkVO.getRoomNo());
				List<MessageVO> history = messageDao.selectList(checkVO.getRoomNo());
				//응답 생성 및 반환
				return RoomDetailResponseVO.builder()
							.room(checkVO)//방정보
							.users(users)//유저목록
							.history(history)
						.build();
			}
		} else {
			//방이 있는지 검사 → 404
			RoomVO roomVO = roomDao.selectOne(roomNo);
			if(roomVO == null) throw new TargetNotfoundException();
			
			//참여자 중에 사용자가 존재하는지 검사 → 403
			String accountType = parseVO.getAccountType();
			if(!accountType.equals("직원")) {
				List<Integer> members = roomDao.getMembers(roomNo);
				if(!members.contains(parseVO.getAccountNo())) throw new GetOutException();
				
				List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);
				if(users.stream()
					.map(user->user.getAccountNo())
					.noneMatch(accountNo->accountNo.equals(parseVO.getAccountNo()))
				) {
					throw new GetOutException();
				}
			}
			if(parseVO.getAccountType().equals("직원")) {
				roomDao.readRoomEmployee(roomNo);
				WebSocketChatVO response = WebSocketChatVO.builder()
						.senderNo(parseVO.getAccountNo())
						.senderName(parseVO.getAccountName())
						.senderType(parseVO.getAccountType())
						.content("채팅 조회")
						.time(LocalDateTime.now())
					.build();
				simpMessagingTemplate.convertAndSend("/public/"+roomNo+"/read", response);
			}
			List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);
			List<MessageVO> history = messageDao.selectList(roomNo);
			
			//응답 생성 및 반환
			return RoomDetailResponseVO.builder()
						.room(roomVO)//방정보
						.users(users)//유저목록
						.history(history)//대화목록
					.build();
		}
	}
	
	//방 상세 정보 
	@GetMapping("/check/{tutorNo}")
	public RoomDetailResponseVO checkTutorChat(@PathVariable(value = "tutorNo", required = false) Integer tutorNo,
						@CurrentUser TokenParseResponseVO parseVO) {
		int accountNo = parseVO.getAccountNo();
		RoomVO checkVO = roomDao.selectOneForTutorCheck(tutorNo, accountNo);
		//생성된 방이 없다면 생성부터
		if(checkVO == null) {
			RoomDto insertDto = new RoomDto().builder()
						.roomNo(roomDao.roomSequence())
						.roomOwner(accountNo)
						.roomType("개인")
					.build();
			roomDao.insertRoom(insertDto);
			roomDao.insertRoomUser(new RoomUserDto().builder()
						.roomUserNo(roomDao.roomUserSequence())
						.roomNo(insertDto.getRoomNo())
						.accountNo(accountNo)
					.build());
			
			List<RoomUserVO> users = roomDao.getMemberInfo(insertDto.getRoomNo());
			//응답 생성 및 반환
			return RoomDetailResponseVO.builder()
						.room(new RoomVO().builder()
									.roomNo(insertDto.getRoomNo())
									.roomOwner(insertDto.getRoomOwner())
									.roomType(insertDto.getRoomType())
									.roomCtime(null)
								.build())//방정보
						.users(users)//유저목록
						.history(new ArrayList<>())
					.build();
		} 
		else {
			List<RoomUserVO> users = roomDao.getMemberInfo(checkVO.getRoomNo());
			List<MessageVO> history = messageDao.selectList(checkVO.getRoomNo());
			//응답 생성 및 반환
			return RoomDetailResponseVO.builder()
						.room(checkVO)//방정보
						.users(users)//유저목록
						.history(history)
					.build();
		}
	}
	
	//방 읽음 처리
	@PutMapping("/{roomNo}/read")
	public void read(@PathVariable(value = "roomNo", required = false) Integer roomNo,
						@CurrentUser TokenParseResponseVO parseVO) {
		if(parseVO.getAccountType().equals("직원")) {
			roomDao.readRoomEmployee(roomNo);
			WebSocketChatVO response = WebSocketChatVO.builder()
					.senderNo(parseVO.getAccountNo())
					.senderName(parseVO.getAccountName())
					.senderType(parseVO.getAccountType())
					.content("채팅 조회")
					.time(LocalDateTime.now())
				.build();
			simpMessagingTemplate.convertAndSend("/public/"+roomNo+"/read", response);
		} else if(parseVO.getAccountType().equals("학생")
				|| parseVO.getAccountType().equals("학부모")) {
			roomDao.readRoomUser(roomNo, parseVO.getAccountNo());
		}
	}
	
	
	//방 참여 관련
	@PostMapping("/{roomNo}/enter")
	public RoomEnterResponseVO enter(@PathVariable(value = "roomNo", required = false) Integer roomNo,
			@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomVO roomVO = roomDao.selectOne(roomNo);
		if(roomVO == null) throw new TargetNotfoundException();
		
		//이미 참여중인지 검사
		List<Integer> members = roomDao.getMembers(roomNo);
		if(members.contains(parseVO.getAccountNo())) {//이미 참여중이면
			return RoomEnterResponseVO.builder()
						.result(true)
						.message("이미 참여중인 방입니다")
					.build();
		}
		
		//인원제한에 걸려있는지 검사 (기본 1명, 1명보다 많으면 담당직원 상담중)
		if(members.size() > 1) {
			return RoomEnterResponseVO.builder()
						.result(false)
						.message("해당 방의 정원이 모두 찼습니다")
					.build();
		}
		
		//(+미래) 차단테이블이 따로 있다면 차단테이블을 조회해서 자격 여부를 판정
		//참여 처리
		int sequence = roomDao.roomUserSequence();
		roomDao.enter(sequence, roomNo, parseVO.getAccountNo());
		LocalDateTime current = LocalDateTime.now();
		/*
		//메세지 생성
		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
			.content("["+parseVO.getAccountName()+"] 님이 입장하셨습니다")
			.level("primary")
			.time(current)
		.build();
		
		//DB저장 처리
		int messageNo = messageDao.sequence();
		messageDao.insertSystem(RoomSystemMessageVO.builder()
					.messageNo(messageNo)
					.messageRoom(roomNo)
					.messageType(response.getType())
					.messageContent(response.getContent())
					.messageTime(Timestamp.valueOf(response.getTime()))
					.messageLevel(response.getLevel())
				.build());
		*/
		//*** 중요 ***
		//enter가 발생하고 나서 (DB에 참여처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);
		simpMessagingTemplate.convertAndSend(
			"/public/"+roomNo+"/users", users
		);
//		//해당 방에 입장 메세지 발송
//		simpMessagingTemplate.convertAndSend(
//			"/public/"+roomNo+"/system", response
//		);
		
		//응답 생성 및 반환
		return RoomEnterResponseVO.builder()
					.result(true)
					.message(roomNo+"번 채팅방에 입장하셨습니다")
				.build();
	}
	
	//방 나가기 매핑
	@PostMapping("/{roomNo}/leave")
	public void leave(@PathVariable(value = "roomNo", required = false) Integer roomNo, 
					@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomVO roomVO = roomDao.selectOne(roomNo);
		if(roomVO == null) throw new TargetNotfoundException();
		
		//참여중인지 검사는 pass
		
		//참여자 제거
		roomDao.leave(roomNo, parseVO.getAccountNo());
		
		//시스템메세지를 해당 방으로 발송
		LocalDateTime current = LocalDateTime.now();
		
		//시스템 메세지 준비
//		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
//					.content("["+parseVO.getAccountName()+"] 님이 퇴장하셨습니다")
//					.level("primary")
//					.time(current)
//				.build();
//		//시스템메세지를 DB에 저장
//		int messageNo = messageDao.sequence();
//		messageDao.insertSystem(RoomSystemMessageVO.builder()
//					.messageNo(messageNo)
//					.messageRoom(roomNo)
//					.messageType(response.getType())
//					.messageContent(response.getContent())
//					.messageTime(Timestamp.valueOf(response.getTime()))
//					.messageLevel(response.getLevel())
//				.build());
//		//시스템 메세지 발송
//		simpMessagingTemplate.convertAndSend(
//				"/public/"+roomNo+"/system", response
//		);
		//*** 중요 ***
		//leave가 발생하고 나서 (DB에 제거처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);
		simpMessagingTemplate.convertAndSend(
			"/public/"+roomNo+"/users", users
		);
		
	}
	
	//방에서 추방하기 매핑
	@PostMapping("/{roomNo}/leave/{accountNo}")
	public void kick(@PathVariable(value = "roomNo", required = false) Integer roomNo,
					@PathVariable(value = "accountNo", required = false) Integer accountNo,
					@CurrentUser TokenParseResponseVO parseVO) {
		//방 존재 여부 검사
		RoomVO roomVO = roomDao.selectOne(roomNo);
		if(roomVO == null) throw new TargetNotfoundException();
		
		//원장인지 검사
		if(!parseVO.getRoleNames().contains("ADMIN"))
			throw new GetOutException();
		
		//참여자 제거
		roomDao.leave(roomNo, accountNo);
		
		//시스템메세지를 해당 방으로 발송
		LocalDateTime current = LocalDateTime.now();
		
		//시스템 메세지 준비
//		WebSocketV4SystemVO response = WebSocketV4SystemVO.builder()
//					.content("["+parseVO.getAccountName()+"] 님이 추방되셨습니다")
//					.level("danger")
//					.time(current)
//				.build();
//		//시스템메세지를 DB에 저장
//		int messageNo = messageDao.sequence();
//		messageDao.insertSystem(RoomSystemMessageVO.builder()
//					.messageNo(messageNo)
//					.messageRoom(roomNo)
//					.messageType(response.getType())
//					.messageContent(response.getContent())
//					.messageTime(Timestamp.valueOf(response.getTime()))
//					.messageLevel(response.getLevel())
//				.build());
		//(+추가) 추방된 대상이 목록으로 튕겨질 수 있도록 행위를 요청 (action 채널)
		simpMessagingTemplate.convertAndSend(
			"/private/"+roomNo+"/action/" + accountNo,
			"leave"
		);
		//시스템 메세지 발송
//		simpMessagingTemplate.convertAndSend(
//			"/public/"+roomNo+"/system", response
//		);
		//*** 중요 ***
		//leave가 발생하고 나서 (DB에 제거처리가 완료되고 나서) 웹소켓으로 인원변동을 알림
		List<RoomUserVO> users = roomDao.getMemberInfo(roomNo);
		simpMessagingTemplate.convertAndSend(
			"/public/"+roomNo+"/users", users
		);
		
	}
}
