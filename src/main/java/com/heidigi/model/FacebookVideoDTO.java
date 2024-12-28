package com.heidigi.model;

import lombok.Data;

@Data
public class FacebookVideoDTO {
	String access_token, upload_phase, video_id, upload_url;
}
