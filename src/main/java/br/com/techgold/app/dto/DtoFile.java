package br.com.techgold.app.dto;

import org.springframework.web.multipart.MultipartFile;

public record DtoFile(
		MultipartFile file,
		Long id
		) {

}
