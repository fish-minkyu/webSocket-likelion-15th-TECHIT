package com.example.chat;

import com.example.chat.dto.ChatMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
// 메시지가 도착했을 때, 어떻게 행동할지 정의하는 추상 클래스
public class SimpleChatHandler extends TextWebSocketHandler { // TextWebSocketHandler: 문자로 들어오는 메시지를 처리하기 위한 Web Socket 구현체
  // 현재 접속한 사용자 목록
  private final List<WebSocketSession> sessions = new ArrayList<>();
  // JSON Serialize 라이브러리
  private final Gson gson = new Gson();

  @Override
  // 연결이 될 때, 무슨 동작을 할지 정의
  public void afterConnectionEstablished(
    // WebSocketSession -> 연결된 사용자 한명
    WebSocketSession session
  ) throws Exception {
    // 사용자 저장
    // : 메시지를 돌려줄 때, 누구한테 보낼지 결정하기 위해 저장해두는 것
    sessions.add(session);
    log.info(session.getId());
    log.info("{} connected, total sessions: {}", session, sessions.size());
  }

  @Override
  // WebSocket을 통해 메시지를 받으면 실행되는 메서드
  protected void handleTextMessage(
    WebSocketSession session,
    TextMessage message
  ) throws Exception {
    // 전달받은 메시지 추출 (Http로 따지면 Request Body다.)
    String payload = message.getPayload();
    log.info("received: {}, from: {}", payload, session.getId());

    // 접속 중인 모든 사용자에게 메시지 전달
    for (WebSocketSession connected: sessions) {
      connected.sendMessage(message);
    }
  }


  @Override
  // WebSocket 연결이 종료될 때 실행
  public void afterConnectionClosed(
    WebSocketSession session,
    CloseStatus status
  ) throws Exception {
    log.info("connection with {} closed:", session.getId());
    sessions.remove(session);
  }

  // 연결되는 모든 사용자에게 메시지 보내기
  public void broadcast(ChatMessage message) throws IOException {
    log.info("broadcasting: {}", message);
    for (WebSocketSession session: sessions) {
      session.sendMessage(new TextMessage(gson.toJson(message)));

    }
  }
}
