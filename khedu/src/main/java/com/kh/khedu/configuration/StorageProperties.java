package com.kh.khedu.configuration;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "custom.storage")
public class StorageProperties {
	private String local;
	private String awsRegion, awsBucket, awsRoot;
	private long presignedLimit;
	
	public File getLocalRoot() {
		return new File(local);
	}
}
