import "../styles/globals.css";
import type { ReactNode } from "react";
import { AuthProvider } from "./(auth)/auth/AuthProvider";

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>{children}</AuthProvider>
      </body>
    </html>
  );
}
