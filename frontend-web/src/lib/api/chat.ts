import { apiClient, ApiResponse } from './client';

export interface ChatRoom {
  roomId: number;
  eventId: number;
  eventTitle: string;
  websocketUrl: string;
  status: 'CREATING' | 'ACTIVE' | 'CLOSING' | 'CLOSED';
  currentParticipants: number;
  maxParticipants: number;
  totalMessages: number;
  startedAt?: string;
  endedAt?: string;
  createdAt: string;
}

export interface ChatMessage {
  messageId: number;
  roomId: number;
  userId: number;
  userDisplayId: string;
  userNickname: string;
  userProfileImageUrl?: string;
  messageType: 'CHAT' | 'JOIN' | 'LEAVE' | 'SYSTEM';
  content: string;
  createdAt: string;
}

export interface ChatMessageRequest {
  roomId: number;
  messageType: 'CHAT' | 'JOIN' | 'LEAVE' | 'SYSTEM';
  content: string;
}

export const chatApi = {
  async createChatRoom(eventId: number): Promise<ApiResponse<ChatRoom>> {
    return apiClient.post<ChatRoom>(`/api/v1/chat/rooms?eventId=${eventId}`);
  },

  async getChatRoom(roomId: number): Promise<ApiResponse<ChatRoom>> {
    return apiClient.get<ChatRoom>(`/api/v1/chat/rooms/${roomId}`);
  },

  async getChatRoomByEventId(eventId: number): Promise<ApiResponse<ChatRoom>> {
    return apiClient.get<ChatRoom>(`/api/v1/chat/rooms/event/${eventId}`);
  },

  async joinChatRoom(roomId: number): Promise<ApiResponse<void>> {
    return apiClient.post<void>(`/api/v1/chat/rooms/${roomId}/join`);
  },

  async leaveChatRoom(roomId: number): Promise<ApiResponse<void>> {
    return apiClient.post<void>(`/api/v1/chat/rooms/${roomId}/leave`);
  },

  async getMessages(roomId: number, page: number = 0, size: number = 50): Promise<ApiResponse<{
    content: ChatMessage[];
    totalPages: number;
    totalElements: number;
    number: number;
  }>> {
    return apiClient.get(`/api/v1/chat/rooms/${roomId}/messages?page=${page}&size=${size}`);
  },

  async deleteMessage(messageId: number): Promise<ApiResponse<void>> {
    return apiClient.delete<void>(`/api/v1/chat/messages/${messageId}`);
  },
};
