'use client';

import { useEffect, useState, useRef } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { chatApi, ChatMessage, ChatRoom } from '@/lib/api/chat';
import { useWebSocket } from '@/lib/hooks/useWebSocket';
import { authApi } from '@/lib/api/auth';

export default function ChatPage() {
  const params = useParams();
  const router = useRouter();
  const eventId = Number(params.id);

  const [chatRoom, setChatRoom] = useState<ChatRoom | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    loadChatRoom();
    loadCurrentUser();
  }, [eventId]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const loadCurrentUser = async () => {
    try {
      const response = await authApi.getCurrentUser();
      setCurrentUserId(response.data.userId);
    } catch (error) {
      console.error('Failed to load current user:', error);
    }
  };

  const loadChatRoom = async () => {
    setLoading(true);
    try {
      const response = await chatApi.getChatRoomByEventId(eventId);
      setChatRoom(response.data);

      // 채팅방 입장
      await chatApi.joinChatRoom(response.data.roomId);

      // 기존 메시지 로드
      const messagesResponse = await chatApi.getMessages(response.data.roomId, 0, 50);
      setMessages(messagesResponse.data.content.reverse());
    } catch (error) {
      console.error('Failed to load chat room:', error);
      alert('채팅방을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleNewMessage = (message: ChatMessage) => {
    setMessages((prev) => [...prev, message]);
  };

  const { connected, sendMessage } = useWebSocket(
    chatRoom?.roomId || 0,
    handleNewMessage
  );

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();

    if (!inputMessage.trim() || !connected) return;

    sendMessage('CHAT', inputMessage);
    setInputMessage('');
  };

  const handleLeaveChat = async () => {
    if (chatRoom) {
      try {
        await chatApi.leaveChatRoom(chatRoom.roomId);
        router.push(`/events/${eventId}`);
      } catch (error) {
        console.error('Failed to leave chat room:', error);
      }
    }
  };

  const getMessageTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      JOIN: '입장',
      LEAVE: '퇴장',
      SYSTEM: '시스템',
    };
    return labels[type] || '';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-500">로딩 중...</p>
      </div>
    );
  }

  if (!chatRoom) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-500">채팅방을 찾을 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-lg font-bold text-gray-900">{chatRoom.eventTitle}</h1>
              <div className="flex items-center gap-4 mt-1 text-sm text-gray-600">
                <span className={`flex items-center gap-1 ${connected ? 'text-green-600' : 'text-red-600'}`}>
                  <span className={`w-2 h-2 rounded-full ${connected ? 'bg-green-600' : 'bg-red-600'}`} />
                  {connected ? '연결됨' : '연결 안됨'}
                </span>
                <span>{chatRoom.currentParticipants}/{chatRoom.maxParticipants}명</span>
              </div>
            </div>
            <button
              onClick={handleLeaveChat}
              className="px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-md"
            >
              나가기
            </button>
          </div>
        </div>
      </header>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto">
        <div className="max-w-4xl mx-auto px-4 py-4 space-y-4">
          {messages.map((message) => {
            const isMyMessage = message.userId === currentUserId;
            const isSystemMessage = message.messageType !== 'CHAT';

            if (isSystemMessage) {
              return (
                <div key={message.messageId} className="text-center">
                  <span className="text-xs text-gray-500">
                    {getMessageTypeLabel(message.messageType)}: {message.content}
                  </span>
                </div>
              );
            }

            return (
              <div
                key={message.messageId}
                className={`flex ${isMyMessage ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-xs ${isMyMessage ? 'items-end' : 'items-start'} flex flex-col gap-1`}>
                  {!isMyMessage && (
                    <div className="flex items-center gap-2">
                      {message.userProfileImageUrl ? (
                        <img
                          src={message.userProfileImageUrl}
                          alt={message.userNickname}
                          className="w-6 h-6 rounded-full"
                        />
                      ) : (
                        <div className="w-6 h-6 rounded-full bg-gray-300" />
                      )}
                      <span className="text-xs font-medium text-gray-700">
                        {message.userNickname || message.userDisplayId}
                      </span>
                    </div>
                  )}
                  <div
                    className={`px-4 py-2 rounded-lg ${
                      isMyMessage
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-900 border border-gray-200'
                    }`}
                  >
                    <p className="text-sm break-words">{message.content}</p>
                  </div>
                  <span className="text-xs text-gray-500">
                    {new Date(message.createdAt).toLocaleTimeString('ko-KR', {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </span>
                </div>
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* Input */}
      <div className="bg-white border-t">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <form onSubmit={handleSendMessage} className="flex gap-2">
            <input
              type="text"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              placeholder="메시지를 입력하세요..."
              disabled={!connected}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:bg-gray-100"
            />
            <button
              type="submit"
              disabled={!connected || !inputMessage.trim()}
              className="px-6 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
            >
              전송
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
