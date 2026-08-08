package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.chat.*;
import kr.adapterz.jpa_practice.dto.chat.ChatRoomResponseDto;
import kr.adapterz.jpa_practice.entity.*;
import kr.adapterz.jpa_practice.exception.AccessDeniedException;
import kr.adapterz.jpa_practice.exception.NotFoundException;
import kr.adapterz.jpa_practice.repository.*;
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
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final kr.adapterz.jpa_practice.service.MessageService messageService;

    @Transactional
    public void createChatRoom(User host, Post post, CreateChatRoomRequestDto request) {

//        User host = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));


//        Post post = postRepository.findById(request.getPostId())
//                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));


        ChatRoom chatRoom = new ChatRoom( 
                host,
                post,
                request.getTitle(),
                request.getSummary()
        );


        post.getChatRoom().add(chatRoom);

        chatRoomRepository.save(chatRoom);

//        return ChatRoomResponseDto.builder()
//                .roomId(chatRoom.getRoomId())
//                .title(chatRoom.getRoomTitle())
//                .summary(chatRoom.getRoomSummary())
//                .participant(chatRoom.getParticipantCount())
//                .build();

    }

    @Transactional
    public ChatRoomResponseDto createChatRoomNotice(Long roomId, ChatRoomNoticeRequestDto request, CustomUserDetails userDetails) {

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

        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .title(chatRoom.getRoomTitle())
                .summary(chatRoom.getRoomSummary())
                .notice(chatRoom.getRoomNotice())
                .participantCount(chatRoom.getParticipant())
                .build();

    }

    @Transactional
    public ChatRoomResponseDto getChatRoom(Long roomId, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_ACCESS_DENIED"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        // 채팅방 인원수 늘리기
        boolean isAlreadyJoined = chatRoomParticipantRepository.existsByUserAndChatRoom(user, chatRoom);

        if(!isAlreadyJoined)
        {
            chatRoomParticipantRepository.save(new ChatRoomParticipant(user, chatRoom));
            chatRoom.increaseParticipant();
        }

        // 이전 채팅 내역 가져오기
        Pageable pageable = PageRequest.of(0, 30);

        List<Chat> chats = chatRepository.findByChatRoom_RoomIdOrderByCreatedAtDesc(roomId, pageable);
        List<ChatMessageResponse> recentChats = chats.stream()
                .map(chat -> new ChatMessageResponse(chat))
                .collect(Collectors.toList());
        return new ChatRoomResponseDto(chatRoom, recentChats);
    }

    @Transactional(readOnly = true)
    public GetparticipantListDto getParticipantList(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("CHATROOM_NOT_FOUND"));
        
        Long hostId = chatRoom.getHost().getUserId();
        
        List<ChatRoomParticipant> participantList = chatRoomParticipantRepository.findByChatRoom(chatRoom);

        List<GetParticitpantDto> dtoList = participantList.stream()
                .map(p -> new GetParticitpantDto(p, p.getUser().getUserId().equals(hostId)))
                .collect(Collectors.toList());

        return new GetparticipantListDto(dtoList);
    }


    @Transactional
    public ChatRoomResponseDto updateChatRoom(Long roomId, UpdateChatRoomRequestDto request, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));


        if(!userDetails.getUserId().equals(chatRoom.getHost().getUserId()))
        {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        if(request.getTitle() != null)
        {
            chatRoom.updateRoomTitle(request.getTitle());
        }

        if(request.getSummary() != null)
        {
            chatRoom.updateRoomSummary(request.getSummary());
        }

        if(request.getNotice() != null)
        {
            chatRoom.assignNotice(request.getNotice());
        }

        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .title(chatRoom.getRoomTitle())
                .summary(chatRoom.getRoomSummary())
                .notice(chatRoom.getRoomNotice())
                .participantCount(chatRoom.getParticipantCount())
                .build();
    }

    @Transactional
    public DeleteChatRoomResponseDto deleteChatRoom(Long roomId, CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        if(!userDetails.getUserId().equals(chatRoom.getHost().getUserId()))
        {
            throw new AccessDeniedException("USER_MISMATCH");
        }

        // 참여자 목록과 채팅 메시지를 먼저 삭제하여 외래키 제약조건 위반 방지
        chatRoomParticipantRepository.deleteByChatRoom(chatRoom);
        chatRepository.deleteByChatRoom(chatRoom);

        chatRoomRepository.delete(chatRoom);

        return new DeleteChatRoomResponseDto(chatRoom);
    }

    @Transactional
    public void leaveChatRoom(Long roomId, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("CHATROOM_NOT_FOUND"));

        if (userDetails.getUserId().equals(chatRoom.getHost().getUserId())) {
            throw new AccessDeniedException("HOST_CANNOT_LEAVE");
        }

        boolean isJoined = chatRoomParticipantRepository.existsByUserAndChatRoom(user, chatRoom);
        if (isJoined) {
            chatRoomParticipantRepository.deleteByUserAndChatRoom(user, chatRoom);
            chatRoom.decreaseParticipant();

            messageService.saveLeaveMessage(roomId, user.getUserId());
        }
    }

}
