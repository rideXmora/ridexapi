package ml.ridex.ridexapi.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import ml.ridex.ridexapi.config.TwilioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("Twilio")
public class TwilioSmsSender implements SMSSender{

    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioSmsSender(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @Override
    public void sendSms(String phone, String message) {
        System.out.println(twilioConfig.getPhone());
        PhoneNumber from = new PhoneNumber(twilioConfig.getPhone());
        PhoneNumber to = new PhoneNumber(phone);
        MessageCreator creator = Message.creator(to, from, message);
        creator.create();
    }
}
