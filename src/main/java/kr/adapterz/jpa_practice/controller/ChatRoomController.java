package kr.adapterz.jpa_practice.controller;

import jakarta.validation.Valid;
import kr.adapterz.jpa_practice.dto.chat.*;
import kr.adapterz.jpa_practice.response.ApiResponse;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import kr.adapterz.jpa_practice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // POST 채팅방 생성
//    @PostMapping("")
//    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createChatRoom(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @Valid @RequestBody CreateChatRoomRequestDto request
//    ) {
//
//        // 순서와 타입(RequestDto, userId)을 서비스 계층과 완벽하게 일치시켰습니다!
//        ChatRoomResponseDto result = chatRoomService.createChatRoom(request, userDetails.getUserId());
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiResponse.of("CHATROOM_CREATED", result));
//
//    }


    // POST 채팅방 입성 후 공지 생성
    @PostMapping("/{chatRoomId}/notice")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createChatRoomNotice(
            @PathVariable("chatRoomId") Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChatRoomNoticeRequestDto request
    ) {

        ChatRoomResponseDto result = chatRoomService.createChatRoomNotice(roomId, request, userDetails);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("CHATROOM_NOTICE_CREATED", result));

    }



    // GET 채팅방 들어가기
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> getChatRoom(
            @PathVariable("chatRoomId") Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ChatRoomResponseDto result = chatRoomService.getChatRoom(roomId, userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("CHATROOM_RETRIEVED", result));
    }


    // PATCH 채팅방 수정하기
    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> updateChatRoom(
        @PathVariable("chatRoomId") Long roomId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody UpdateChatRoomRequestDto request
    ){
        ChatRoomResponseDto result = chatRoomService.updateChatRoom(roomId, request, userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("CHATROOM_UPDATE", result));
    }




    // DELETE 채팅방 삭제하기
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<DeleteChatRoomResponseDto>> deleteChatRoom(
            @PathVariable("chatRoomId") Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteChatRoomResponseDto result = chatRoomService.deleteChatRoom(roomId, userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("CHATROOM_DELETE", result));
    }

}
