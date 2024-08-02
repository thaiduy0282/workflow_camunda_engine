package com.qworks.camunda.client;

public enum ProcessStatus {
    CREATED,        // Quy trình được tạo ra nhưng chưa bắt đầu
    IN_PROGRESS,    // Quy trình đang được thực thi
    COMPLETED,      // Quy trình đã hoàn thành
    FAILED,         // Quy trình thất bại trong quá trình thực thi
    SUSPENDED,      // Quy trình bị tạm dừng
    CANCELLED,      // Quy trình bị hủy bỏ
    UNKNOWN         // Trạng thái không xác định hoặc chưa được cập nhật
}
