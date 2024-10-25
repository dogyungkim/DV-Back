package org.richardstallman.dvback.domain.S3;

import java.util.Map;

/**
 * S3와 관련된 정보를 제공하는 서비스 인터페이스입니다.
 */
public interface S3Service {
    /*
    * 클라이언트 사이드에서 파일 업로드를 할 수 있는 PreSignedURL을 반환합니다.
    * @param fileName 저장할 파일의 이름 metadata 저장할 파일의 metadata
    * @return 생성된 PreSignedURL
    * */
    String createPreSignedURL(String fileName, Map<String,String> metadata);

    /*
    * S3에 저장된 파일을 다운 받을 수 있는 URL을 반환합니다.
    * @parma fileName 다운로드 할 파일이름
    * @return 파일을 다운로드 할 수 있는 URL
    * */
    String getDownloadURL(String fileName);
}
