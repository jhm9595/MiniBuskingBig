import { apiClient } from './api-client';

export interface Venue {
  id: number;
  name: string;
  description?: string;
  address: string;
  addressDetail?: string;
  postalCode?: string;
  latitude: number;
  longitude: number;
  capacity: number;
  hourlyRate: number;
  facilities?: string;
  photos?: string;
  openTime: string;
  closeTime: string;
  contactPhone?: string;
  contactEmail?: string;
  status: 'PENDING' | 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  rejectionReason?: string;
  approvedAt?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface VenueBooking {
  id: number;
  venue: Venue;
  startTime: string;
  endTime: string;
  durationHours: number;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  purpose?: string;
  specialRequests?: string;
  cancellationReason?: string;
  confirmedAt?: string;
  cancelledAt?: string;
  createdAt: string;
}

export const venueApi = {
  // 장소 등록
  createVenue: async (data: {
    name: string;
    description?: string;
    address: string;
    addressDetail?: string;
    postalCode?: string;
    latitude: number;
    longitude: number;
    capacity: number;
    hourlyRate: number;
    openTime: string;
    closeTime: string;
    contactPhone?: string;
    contactEmail?: string;
  }) => {
    return apiClient.post<Venue>('/api/venues', data);
  },

  // 장소 수정
  updateVenue: async (venueId: number, data: Partial<Venue>) => {
    return apiClient.put<Venue>(`/api/venues/${venueId}`, data);
  },

  // 장소 조회
  getVenue: async (venueId: number) => {
    return apiClient.get<Venue>(`/api/venues/${venueId}`);
  },

  // 활성 장소 목록
  getActiveVenues: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: Venue[] }>(`/api/venues?page=${page}&size=${size}`);
  },

  // 내 장소 목록
  getMyVenues: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: Venue[] }>(`/api/venues/my?page=${page}&size=${size}`);
  },

  // 주변 장소 검색
  getNearbyVenues: async (latitude: number, longitude: number, radiusKm: number = 5) => {
    return apiClient.get<Venue[]>(
      `/api/venues/nearby?latitude=${latitude}&longitude=${longitude}&radiusKm=${radiusKm}`
    );
  },

  // 장소 검색
  searchVenues: async (keyword: string, page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: Venue[] }>(
      `/api/venues/search?keyword=${keyword}&page=${page}&size=${size}`
    );
  },

  // 장소 활성화/비활성화
  toggleVenueStatus: async (venueId: number) => {
    return apiClient.patch<Venue>(`/api/venues/${venueId}/toggle`);
  },

  // 장소 삭제
  deleteVenue: async (venueId: number) => {
    return apiClient.delete(`/api/venues/${venueId}`);
  },

  // 시설 정보 업데이트
  updateFacilities: async (venueId: number, facilities: string) => {
    return apiClient.patch<Venue>(`/api/venues/${venueId}/facilities`, { facilities });
  },

  // 사진 업데이트
  updatePhotos: async (venueId: number, photos: string) => {
    return apiClient.patch<Venue>(`/api/venues/${venueId}/photos`, { photos });
  },
};

export const venueBookingApi = {
  // 장소 예약
  createBooking: async (data: {
    venueId: number;
    startTime: string;
    endTime: string;
    purpose?: string;
    specialRequests?: string;
  }) => {
    return apiClient.post<VenueBooking>('/api/venue-bookings', data);
  },

  // 예약 확정
  confirmBooking: async (bookingId: number) => {
    return apiClient.post<VenueBooking>(`/api/venue-bookings/${bookingId}/confirm`);
  },

  // 예약 취소
  cancelBooking: async (bookingId: number, reason: string) => {
    return apiClient.post<VenueBooking>(`/api/venue-bookings/${bookingId}/cancel`, { reason });
  },

  // 예약 조회
  getBooking: async (bookingId: number) => {
    return apiClient.get<VenueBooking>(`/api/venue-bookings/${bookingId}`);
  },

  // 내 예약 목록
  getMyBookings: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: VenueBooking[] }>(
      `/api/venue-bookings/my?page=${page}&size=${size}`
    );
  },

  // 장소별 예약 목록
  getVenueBookings: async (venueId: number, page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: VenueBooking[] }>(
      `/api/venue-bookings/venue/${venueId}?page=${page}&size=${size}`
    );
  },

  // 제공자별 예약 목록
  getProviderBookings: async (page: number = 0, size: number = 20) => {
    return apiClient.get<{ content: VenueBooking[] }>(
      `/api/venue-bookings/provider?page=${page}&size=${size}`
    );
  },

  // 예약 가능 여부 확인
  checkAvailability: async (venueId: number, startTime: string, endTime: string) => {
    return apiClient.get<boolean>(
      `/api/venue-bookings/check-availability?venueId=${venueId}&startTime=${startTime}&endTime=${endTime}`
    );
  },

  // 예약된 시간대 조회
  getBookedSlots: async (venueId: number, date: string) => {
    return apiClient.get<VenueBooking[]>(
      `/api/venue-bookings/booked-slots?venueId=${venueId}&date=${date}`
    );
  },

  // 이벤트 연결
  linkToEvent: async (bookingId: number, eventId: number) => {
    return apiClient.post<VenueBooking>(`/api/venue-bookings/${bookingId}/link-event`, {
      eventId,
    });
  },
};
