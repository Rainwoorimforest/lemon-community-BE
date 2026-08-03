package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.chat.*;
import kr.adapterz.jpa_practice.dto.chat.CreateChatRoomResponseDto;
import kr.adapterz.jpa_practice.entity.Chat;
import kr.adapterz.jpa_practice.entity.ChatRoom;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.repository.ChatRepository;
import kr.adapterz.jpa_practice.repository.ChatRoomRepository;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public CreateChatRoomResponseDto createChatRoom(CreateChatRoomRequestDto request, Long userId) {

        User host = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));


        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));


        ChatRoom chatRoom = new ChatRoom( 
                host,
                post,
                request.getTitle(),
                request.getSummary()
        );

        chatRoomRepository.save(chatRoom);

        return CreateChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .title(chatRoom.getRoomTitle())
                .summary(chatRoom.getRoomSummary())
                .participant(chatRoom.getParticipantCount())
                .build();

    }

    @Transactional
    public CreateChatRoomResponseDto createChatRoomNotice(Long roomId, ChatRoomNoticeRequestDto request, CustomUserDetails userDetails) {

        User host = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND")); // 유저플로우 자체가 post 게시글 상세에서 채팅방생성하는 건데 이게 필요한 코드일까? 그래도 넣어야하나? 유저플로우를 믿지마라?

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));


        if(!userDetails.getUserId().equals(chatRoom.getHost().getUserId())) {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        chatRoom.assignNotice(request.getNotice());

        return CreateChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .title(chatRoom.getRoomTitle())
                .summary(chatRoom.getRoomSummary())
                .notice(chatRoom.getRoomNotice())
                .participant(chatRoom.getParticipant())
                .build();

    }

    @Transactional //언제 readonly = true지?
    public GetChatRoomResponseDto getChatRoom(Long roomId, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_ACCESS_DENIED"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        // 채팅방 인원수 늘리기
        chatRoom.countParticipant();

        // 이전 채팅 내역 가져오기
        Pageable pageable = PageRequest.of(0, 30);

        List<Chat> chats = chatRepository.findByChatRoom_RoomIdOrderByCreatedAtDesc(roomId, pageable);
        List<ChatMessageResponse> recentChats = chats.stream()
                .map(chat -> new ChatMessageResponse(chat))
                .collect(Collectors.toList());
        return new GetChatRoomResponseDto(chatRoom, recentChats);
    }





}
