import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'MiniBuskingBig',
  description: 'iOS, Android, Web 크로스플랫폼 애플리케이션',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
