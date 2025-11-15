'use client';

import { useEffect, useState } from 'react';
import { eventsApi, Event } from '@/lib/api/events';

export default function HomePage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'all' | 'live' | 'upcoming'>('all');

  useEffect(() => {
    loadEvents();
  }, [filter]);

  const loadEvents = async () => {
    setLoading(true);
    try {
      let response;
      if (filter === 'live') {
        response = await eventsApi.getLiveEvents();
      } else if (filter === 'upcoming') {
        response = await eventsApi.getUpcomingEvents();
      } else {
        response = await eventsApi.getAllEvents();
      }
      setEvents(response.data);
    } catch (error) {
      console.error('Failed to load events:', error);
    } finally {
      setLoading(false);
    }
  };

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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-gray-900">MiniBuskingBig</h1>
            <div className="flex gap-4">
              <a
                href="/login"
                className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900"
              >
                로그인
              </a>
              <a
                href="/singer/profile"
                className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
              >
                가수 등록
              </a>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filters */}
        <div className="mb-6 flex gap-2">
          <button
            onClick={() => setFilter('all')}
            className={`px-4 py-2 rounded-md text-sm font-medium ${
              filter === 'all'
                ? 'bg-indigo-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            전체
          </button>
          <button
            onClick={() => setFilter('live')}
            className={`px-4 py-2 rounded-md text-sm font-medium ${
              filter === 'live'
                ? 'bg-indigo-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            라이브
          </button>
          <button
            onClick={() => setFilter('upcoming')}
            className={`px-4 py-2 rounded-md text-sm font-medium ${
              filter === 'upcoming'
                ? 'bg-indigo-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            예정
          </button>
        </div>

        {/* Events Grid */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-500">로딩 중...</p>
          </div>
        ) : events.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">공연이 없습니다.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {events.map((event) => (
              <div
                key={event.eventId}
                className="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow overflow-hidden"
              >
                <div className="p-6">
                  <div className="flex items-start justify-between mb-3">
                    <h3 className="text-lg font-semibold text-gray-900 line-clamp-1">
                      {event.title}
                    </h3>
                    <span
                      className={`px-2 py-1 text-xs font-medium text-white rounded ${getStatusBadge(
                        event.status
                      )}`}
                    >
                      {getStatusText(event.status)}
                    </span>
                  </div>

                  {event.description && (
                    <p className="text-sm text-gray-600 mb-4 line-clamp-2">
                      {event.description}
                    </p>
                  )}

                  <div className="space-y-2 text-sm text-gray-500">
                    {event.singerStageName && (
                      <p className="flex items-center gap-2">
                        <span className="font-medium">가수:</span>
                        <span>{event.singerStageName}</span>
                      </p>
                    )}
                    <p className="flex items-center gap-2">
                      <span className="font-medium">시작:</span>
                      <span>{new Date(event.startTime).toLocaleString('ko-KR')}</span>
                    </p>
                    {event.venueAddress && (
                      <p className="flex items-center gap-2">
                        <span className="font-medium">장소:</span>
                        <span className="line-clamp-1">{event.venueAddress}</span>
                      </p>
                    )}
                  </div>

                  <div className="mt-4 flex items-center gap-4 text-xs text-gray-500">
                    <span>조회 {event.viewCount}</span>
                    <span>좋아요 {event.favoriteCount}</span>
                  </div>

                  <a
                    href={`/events/${event.eventId}`}
                    className="mt-4 block w-full px-4 py-2 text-center text-sm font-medium text-indigo-600 bg-indigo-50 rounded-md hover:bg-indigo-100"
                  >
                    자세히 보기
                  </a>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
