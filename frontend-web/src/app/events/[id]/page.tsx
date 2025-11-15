'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { eventsApi, Event } from '@/lib/api/events';
import { chatApi } from '@/lib/api/chat';
import { paymentApi } from '@/lib/api/payment';

export default function EventDetailPage() {
  const params = useParams();
  const eventId = Number(params.id);

  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [hasChatRoom, setHasChatRoom] = useState(false);

  useEffect(() => {
    loadEvent();
    checkChatRoom();
  }, [eventId]);

  const loadEvent = async () => {
    setLoading(true);
    try {
      const response = await eventsApi.getEvent(eventId);
      setEvent(response.data);
    } catch (error) {
      console.error('Failed to load event:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkChatRoom = async () => {
    try {
      await chatApi.getChatRoomByEventId(eventId);
      setHasChatRoom(true);
    } catch (error) {
      setHasChatRoom(false);
    }
  };

  const handleCreateChatPayment = async () => {
    try {
      const payment = await paymentApi.createChatPayment(eventId);
      alert(`채팅 결제가 생성되었습니다.\n주문번호: ${payment.data.orderId}\n금액: ${payment.data.amount}원`);
      // 실제로는 토스페이먼츠 결제 창으로 이동
    } catch (error) {
      console.error('Failed to create payment:', error);
      alert('결제 생성에 실패했습니다.');
    }
  };

  const handleJoinChat = () => {
    window.location.href = `/events/${eventId}/chat`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-500">로딩 중...</p>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-500">공연을 찾을 수 없습니다.</p>
      </div>
    );
  }

  const getStatusBadge = (status: string) => {
    const badges = {
      SCHEDULED: 'bg-blue-500',
      LIVE: 'bg-green-500',
      ENDED: 'bg-gray-500',
      CANCELLED: 'bg-red-500',
    };
    return badges[status as keyof typeof badges] || 'bg-gray-500';
  };

  const getStatusText = (status: string) => {
    const texts = {
      SCHEDULED: '예정',
      LIVE: '라이브',
      ENDED: '종료',
      CANCELLED: '취소',
    };
    return texts[status as keyof typeof texts] || status;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <a href="/" className="text-indigo-600 hover:text-indigo-800 text-sm font-medium">
            ← 목록으로
          </a>
        </div>
      </header>

      {/* Content */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <div className="p-6">
            {/* Title and Status */}
            <div className="flex items-start justify-between mb-4">
              <h1 className="text-2xl font-bold text-gray-900">{event.title}</h1>
              <span
                className={`px-3 py-1 text-sm font-medium text-white rounded ${getStatusBadge(
                  event.status
                )}`}
              >
                {getStatusText(event.status)}
              </span>
            </div>

            {/* Description */}
            {event.description && (
              <p className="text-gray-700 mb-6 whitespace-pre-wrap">{event.description}</p>
            )}

            {/* Info Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
              {event.singerStageName && (
                <div className="flex items-start gap-2">
                  <span className="text-sm font-medium text-gray-500 min-w-20">가수:</span>
                  <span className="text-sm text-gray-900">{event.singerStageName}</span>
                </div>
              )}
              {event.teamName && (
                <div className="flex items-start gap-2">
                  <span className="text-sm font-medium text-gray-500 min-w-20">팀:</span>
                  <span className="text-sm text-gray-900">{event.teamName}</span>
                </div>
              )}
              <div className="flex items-start gap-2">
                <span className="text-sm font-medium text-gray-500 min-w-20">시작 시간:</span>
                <span className="text-sm text-gray-900">
                  {new Date(event.startTime).toLocaleString('ko-KR')}
                </span>
              </div>
              <div className="flex items-start gap-2">
                <span className="text-sm font-medium text-gray-500 min-w-20">종료 시간:</span>
                <span className="text-sm text-gray-900">
                  {new Date(event.endTime).toLocaleString('ko-KR')}
                </span>
              </div>
              {event.venueAddress && (
                <div className="flex items-start gap-2 col-span-2">
                  <span className="text-sm font-medium text-gray-500 min-w-20">장소:</span>
                  <span className="text-sm text-gray-900">{event.venueAddress}</span>
                </div>
              )}
            </div>

            {/* Stats */}
            <div className="flex items-center gap-6 text-sm text-gray-500 mb-6 pb-6 border-b">
              <span>조회 {event.viewCount}</span>
              <span>좋아요 {event.favoriteCount}</span>
            </div>

            {/* Chat Section */}
            {event.chatEnabled && (
              <div className="bg-indigo-50 rounded-lg p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-gray-900 mb-1">채팅 참여</h3>
                    <p className="text-sm text-gray-600">
                      실시간 채팅으로 다른 관객들과 소통하세요
                    </p>
                    {event.chatMaxParticipants && (
                      <p className="text-xs text-gray-500 mt-1">
                        최대 {event.chatMaxParticipants}명
                      </p>
                    )}
                  </div>
                  {hasChatRoom ? (
                    <button
                      onClick={handleJoinChat}
                      className="px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700"
                    >
                      채팅 참여
                    </button>
                  ) : (
                    <button
                      onClick={handleCreateChatPayment}
                      className="px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700"
                    >
                      채팅 결제 (1,000원)
                    </button>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
