package ml.ridex.ridexapi.service;

public interface SMSSender {
    void sendSms(String phone, String message);
}
