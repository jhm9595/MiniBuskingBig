'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { venueApi, venueBookingApi, Venue, VenueBooking } from '@/lib/api/venue';

export default function VenueDetailPage() {
  const params = useParams();
  const router = useRouter();
  const venueId = Number(params.id);

  const [venue, setVenue] = useState<Venue | null>(null);
  const [loading, setLoading] = useState(true);
  const [showBookingModal, setShowBookingModal] = useState(false);

  const [bookingForm, setBookingForm] = useState({
    startTime: '',
    endTime: '',
    purpose: '',
    specialRequests: '',
  });

  useEffect(() => {
    loadVenue();
  }, [venueId]);

  const loadVenue = async () => {
    try {
      setLoading(true);
      const response = await venueApi.getVenue(venueId);
      setVenue(response.data);
    } catch (error) {
      console.error('Failed to load venue:', error);
      alert('장소 정보를 불러올 수 없습니다.');
      router.push('/venues');
    } finally {
      setLoading(false);
    }
  };

  const handleBooking = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await venueBookingApi.createBooking({
        venueId,
        startTime: bookingForm.startTime,
        endTime: bookingForm.endTime,
        purpose: bookingForm.purpose,
        specialRequests: bookingForm.specialRequests,
      });

      alert('예약이 완료되었습니다!');
      setShowBookingModal(false);
      setBookingForm({
        startTime: '',
        endTime: '',
        purpose: '',
        specialRequests: '',
      });
    } catch (error) {
      console.error('Failed to create booking:', error);
      alert('예약에 실패했습니다.');
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR').format(amount);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">로딩 중...</p>
        </div>
      </div>
    );
  }

  if (!venue) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <a href="/venues" className="text-indigo-600 hover:text-indigo-800 text-sm font-medium">
            ← 장소 목록
          </a>
        </div>
      </header>

      {/* Content */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hero Image */}
        <div className="w-full h-96 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg mb-8 flex items-center justify-center">
          <svg
            className="w-24 h-24 text-white"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
            />
          </svg>
        </div>

        {/* Venue Info */}
        <div className="bg-white rounded-lg shadow-sm p-8 mb-6">
          <div className="flex items-start justify-between mb-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{venue.name}</h1>
              <p className="text-gray-600">{venue.description}</p>
            </div>
            <button
              onClick={() => setShowBookingModal(true)}
              className="px-6 py-3 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700"
            >
              예약하기
            </button>
          </div>

          <div className="space-y-4 mb-8">
            <div className="flex items-start gap-3">
              <svg className="w-6 h-6 text-gray-400 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <div>
                <p className="text-gray-900">{venue.address}</p>
                {venue.addressDetail && <p className="text-gray-600 text-sm">{venue.addressDetail}</p>}
              </div>
            </div>

            <div className="flex items-center gap-3">
              <svg className="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <p className="text-gray-900">최대 수용 인원: {venue.capacity}명</p>
            </div>

            <div className="flex items-center gap-3">
              <svg className="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p className="text-gray-900">운영 시간: {venue.openTime} - {venue.closeTime}</p>
            </div>

            {venue.contactPhone && (
              <div className="flex items-center gap-3">
                <svg className="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                <p className="text-gray-900">{venue.contactPhone}</p>
              </div>
            )}

            {venue.contactEmail && (
              <div className="flex items-center gap-3">
                <svg className="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <p className="text-gray-900">{venue.contactEmail}</p>
              </div>
            )}
          </div>

          <div className="pt-6 border-t border-gray-200">
            <div className="flex items-baseline gap-2">
              <span className="text-4xl font-bold text-indigo-600">
                {formatCurrency(venue.hourlyRate)}
              </span>
              <span className="text-lg text-gray-600">원/시간</span>
            </div>
          </div>
        </div>
      </main>

      {/* Booking Modal */}
      {showBookingModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">장소 예약</h2>

            <form onSubmit={handleBooking} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  시작 시간
                </label>
                <input
                  type="datetime-local"
                  value={bookingForm.startTime}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, startTime: e.target.value })
                  }
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  종료 시간
                </label>
                <input
                  type="datetime-local"
                  value={bookingForm.endTime}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, endTime: e.target.value })
                  }
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  예약 목적
                </label>
                <input
                  type="text"
                  value={bookingForm.purpose}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, purpose: e.target.value })
                  }
                  placeholder="예: 버스킹 공연"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  특별 요청사항
                </label>
                <textarea
                  value={bookingForm.specialRequests}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, specialRequests: e.target.value })
                  }
                  rows={3}
                  placeholder="특별한 요청사항이 있으시면 입력해주세요"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-600"
                />
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowBookingModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium"
                >
                  예약하기
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
