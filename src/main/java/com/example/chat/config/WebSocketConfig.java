package com.example.chat.config;

import com.example.chat.SimpleChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
  private final SimpleChatHandler simpleChatHandler;

  @Override
  public void registerWebSocketHandlers(
    // 웹소켓 연결을 관리하는 registry
    WebSocketHandlerRegistry registry
  ) {
    // "ws/chat" 경로로 웹 소켓 연결을 시도한다.
    // 그렇다면 simpleChatHandler 클래스를 이용하여 어떻게 동작할지 정의한다.
    registry.addHandler(simpleChatHandler, "ws/chat")
      .setAllowedOrigins("*");
  }
}
