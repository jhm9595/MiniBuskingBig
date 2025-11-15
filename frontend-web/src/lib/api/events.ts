import { apiClient, ApiResponse } from './client';

export interface Event {
  eventId: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  venueAddress?: string;
  venueLat?: number;
  venueLng?: number;
  chatEnabled: boolean;
  chatMaxParticipants?: number;
  chatPaymentStatus?: string;
  status: 'SCHEDULED' | 'LIVE' | 'ENDED' | 'CANCELLED';
  viewCount: number;
  favoriteCount: number;
  createdAt: string;
  updatedAt: string;
  singerId?: number;
  singerStageName?: string;
  teamId?: number;
  teamName?: string;
}

export interface CreateEventRequest {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  venueAddress?: string;
  venueLat?: number;
  venueLng?: number;
  chatEnabled?: boolean;
  chatMaxParticipants?: number;
  teamId?: number;
}

export const eventsApi = {
  async getAllEvents(): Promise<ApiResponse<Event[]>> {
    return apiClient.get<Event[]>('/api/v1/events');
  },

  async getLiveEvents(): Promise<ApiResponse<Event[]>> {
    return apiClient.get<Event[]>('/api/v1/events/live');
  },

  async getUpcomingEvents(): Promise<ApiResponse<Event[]>> {
    return apiClient.get<Event[]>('/api/v1/events/upcoming');
  },

  async getEvent(eventId: number): Promise<ApiResponse<Event>> {
    return apiClient.get<Event>(`/api/v1/events/${eventId}`);
  },

  async createEvent(data: CreateEventRequest): Promise<ApiResponse<Event>> {
    return apiClient.post<Event>('/api/v1/events', data);
  },

  async updateEvent(eventId: number, data: Partial<CreateEventRequest>): Promise<ApiResponse<Event>> {
    return apiClient.put<Event>(`/api/v1/events/${eventId}`, data);
  },

  async deleteEvent(eventId: number): Promise<ApiResponse<void>> {
    return apiClient.delete<void>(`/api/v1/events/${eventId}`);
  },

  async startEvent(eventId: number): Promise<ApiResponse<void>> {
    return apiClient.post<void>(`/api/v1/events/${eventId}/start`);
  },

  async endEvent(eventId: number): Promise<ApiResponse<void>> {
    return apiClient.post<void>(`/api/v1/events/${eventId}/end`);
  },
};
