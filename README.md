# Web Socket

- 2024.02.22 `15주차`

- chat.html: 채팅방 역할을 하는 페이지(입장 메시지, 퇴장 메시지, 채팅 기록)
- rooms.html: 로비 역할을 하는 페이지

해당 프로젝트는 전체 채팅방을 가정으로 구현이 되었다.  
1:1 채팅방을 만들고 싶다 접속 사용자 ID를 잘 분리시켜주면 된다.  
(하지만, 그리 쉬운 로직은 아니다.)

## 스팩

- Spring Boot 3.2.2
- Spring Web
- Lombok
- WebSocket
- thymeleaf
- gson 2.10.1: JSON 직렬화 라이브러리

## Key Point

[chat.html](/src/main/resources/templates/chat.html)  
- WebSocket 생성
```javascript
const webSocket = new WebSocket('ws://localhost:8080/ws/chat')
```

-  접속 연결이 되었을 때, 실행
```javascript
    webSocket.onopen = (event) => {
        console.log(event)
        webSocket.send(JSON.stringify({
            username,
            message: `${username} 입장`
        }))
    }
```

- 메시지를 보낼 때, 실행
```javascript
    webSocket.onmessage = (msg) => {
        console.log(msg)
        const data = JSON.parse(msg.data)
        const chatMessage = document.createElement('div')
        const message = document.createElement('p')
        message.innerText = data.username + ': ' + data.message;

        chatMessage.appendChild(message)
        document.getElementById('response').appendChild(chatMessage)
    }
```

- 접속이 종료되었을 때, 실행
```javascript
    webSocket.onclose = (event) => {
        console.log(event)
        webSocket.send(JSON.stringify({
            username,
            message: `${username} 퇴장`
        }))
    }
```

[WebSocketConfig](/src/main/java/com/example/chat/config/WebSocketConfig.java)
```java
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
```

[SimpleChatHandler](/src/main/java/com/example/chat/SimpleChatHandler.java)

- afterConnectionEstablished  
=> 연결이 될 때, 무슨 동작을 할지 정의
```java
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
```

- handleTextMessage  
=> WebSocket을 통해 메시지를 받으면 실행되는 메서드
```java
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
```

- afterConnectionClosed  
=> WebSocket 연결이 종료될 때 실행
```java
  @Override
  // WebSocket 연결이 종료될 때 실행
  public void afterConnectionClosed(
    WebSocketSession session,
    CloseStatus status
  ) throws Exception {
    log.info("connection with {} closed:", session.getId());
    sessions.remove(session);
  }
```

- broadcast  
=> 연결되는 모든 사용자에게 메시지 보내기
```java
  public void broadcast(ChatMessage message) throws IOException {
    log.info("broadcasting: {}", message);
    for (WebSocketSession session: sessions) {
      session.sendMessage(new TextMessage(gson.toJson(message)));

    }
  }
```

## GitHub

- 강사님 GitHub  
[likelion-backend-8-ws](https://github.com/edujeeho0/likelion-backend-8-ws)

