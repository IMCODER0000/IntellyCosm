package gachonproject.mobile.domain.em;

/**
 * 분석 상태를 나타내는 열거형 클래스입니다.
 */
public enum AnalysisStatus {
    PENDING,    // 대기 중
    PROCESSING, // 처리 중
    COMPLETED,  // 완료됨
    FAILED      // 실패함
}
