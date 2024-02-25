package com.example.chat;

import com.example.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
  private final SimpleChatHandler chatHandler;

  @GetMapping("/lobby")
  public String lobby() {
    return "rooms";
  }

  @GetMapping("/enter")
  public String enter() {
    return "chat";
  }

  // 관리자가 전체 공지하는 API
  @PostMapping("/broadcast")
  public @ResponseBody String broadcast(
    @RequestBody
    ChatMessage message
  ) throws IOException {
    chatHandler.broadcast(message);
    return "done";
  }
}
