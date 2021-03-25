package com.timcircle.keeper.react;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.timcircle.keeper.util.ContentUtil;
import com.timcircle.keeper.util.JsonUtil;

public class SendEmailReaction implements IReaction {

	private static Logger logger = Logger.getLogger(SendEmailReaction.class);

	@Override
	public void react(Map<String, Object> args, Map<String, Object> jobBundle) throws Exception {
		JsonUtil json = new JsonUtil(args);

		List<String> toList = json.getStringList("to");
		if (toList.size() == 0) {
			logger.info("There is no receiver to send email.");
			return;
		}

		JsonUtil smtp = json.getJson("smtp");
		String host = smtp.getString("host");
		int port = smtp.getInt("port", 587);

		InternetAddress[] address = null;
		String to = String.join(",", toList);
		String From = smtp.getString("reply_email");
		String Subject = json.getString("subject");
		String type = "text/html";
		String messageText = json.getString("message");
		String authUser = smtp.getString("username");
		String authPwd = smtp.getString("password");

		// �B�z�T�����e
		List<Map<String, Object>> replaceList = json.getMapList("replace");
		if (replaceList.size() > 0) {
			Subject = ContentUtil.handleContent(Subject, replaceList, jobBundle);
			messageText = ContentUtil.handleContent(messageText, replaceList, jobBundle);
		}
		// �]�w�ҭn�Ϊ�Mail ���A���M�ҨϥΪ��ǰe��w
		java.util.Properties props = System.getProperties();
		props.put("mail.smtp.port", port);
		props.put("mail.host", host);
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");

		Session mailSession = Session.getInstance(props);
		MimeMessage msg = new MimeMessage(mailSession);

		// �]�w�ǰe�l�󪺵o�H�H
		try {
			msg.setFrom(new InternetAddress(From));
			// �]�w�ǰe�l��ܦ��H�H���H�c
			address = InternetAddress.parse(to, false);
			msg.setRecipients(Message.RecipientType.TO, address);

			// �]�w�H�����D�D
			Subject = javax.mail.internet.MimeUtility.encodeText(Subject, "UTF-8", null);
			msg.setSubject(Subject);
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setContent(messageText, type + ";charset=UTF-8");

			// �]�w�e�H���ɶ�
			msg.setSentDate(new Date());

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			msg.setContent(mp);

			Transport transport = mailSession.getTransport("smtp");
			transport.connect(host, authUser, authPwd);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			logger.info("The email sent to " + to);
		} catch (MessagingException e) {
			logger.error("sendNewPasswordForUserEmail fail MessagingException.", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("sendNewPasswordForUserEmail fail UnsupportedEncodingException.", e);
		}
	}

//	private String handleContent(String content, List<Map<String, Object>> replaceList, Map<String, Object> jobBundle) {
//		for (Map<String, Object> r : replaceList) {
//			JsonUtil replace = new JsonUtil(r);
//			for (String key : r.keySet()) {
//				String param = "$" + key;
//				if (!content.contains(param))
//					continue;
//
//				JsonUtil item = replace.getJson(key);
//				String bundle = item.getString("bundle");
//				String[] bundlePaths = bundle.split("/");
//				if (bundlePaths.length > 0) {
//					String bundleName = bundlePaths[0];
//					JsonUtil jb = new JsonUtil(jobBundle);
//					if (jobBundle.containsKey(bundleName)) {
//						List<String> keys = new ArrayList<>();
//						for (int i = 1; i < bundlePaths.length; i++) {
//							keys.add(bundlePaths[i]);
//						}
//
//						Map<String, Object> b = jb.getMap(bundleName);
//						String value = findBundleValue(b, keys);
//						List<JsonUtil> converters = item.getJsonArray("converters");
//						for (JsonUtil converter : converters) {
//							String iif = converter.getString("if");
//							if (StringUtil.equalsStr(iif, value)) {
//								String converted = converter.getString("then");
//								return content.replace(param, converted);
//							}
//						}
//					}
//				}
//			}
//		}
//		return content;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private String findBundleValue(Map bundle, List<String> keys) {
//		String key = keys.get(0);
//		keys.remove(0);
//
//		if (keys.size() == 0) {
//			if (bundle.containsKey(key)) {
//				Object value = bundle.get(key);
//				if (value instanceof String)
//					return (String) value;
//				return String.valueOf(value);
//			}
//		} else {
//			if (bundle.containsKey(key)) {
//				Object value = bundle.get(key);
//				if (value instanceof Map) {
//					return findBundleValue((Map) value, keys);
//				}
//			}
//		}
//
//		return StringUtil.EMPTY;
//	}

}
