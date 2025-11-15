import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface WebSocketMessage {
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

export function useWebSocket(roomId: number, onMessage: (message: WebSocketMessage) => void) {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log('WebSocket Connected');
      setConnected(true);

      // 채팅방 구독
      client.subscribe(`/topic/chat/${roomId}`, (message) => {
        const data = JSON.parse(message.body) as WebSocketMessage;
        onMessage(data);
      });
    };

    client.onStompError = (frame) => {
      console.error('STOMP Error:', frame);
      setConnected(false);
    };

    client.onDisconnect = () => {
      console.log('WebSocket Disconnected');
      setConnected(false);
    };

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [roomId, onMessage]);

  const sendMessage = (messageType: 'CHAT' | 'JOIN' | 'LEAVE' | 'SYSTEM', content: string) => {
    if (clientRef.current && connected) {
      clientRef.current.publish({
        destination: '/app/chat.send',
        body: JSON.stringify({
          roomId,
          messageType,
          content,
        }),
      });
    }
  };

  return { connected, sendMessage };
}
