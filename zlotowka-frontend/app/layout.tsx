import "./globals.css";
import Sidebar from "@/components/Sidebar";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
      <html lang="en">
        <body className="flex min-h-screen">
        <div className="w-96 h-screen sticky left-0 top-0">
          <Sidebar/>
        </div>
        <div className="w-full">
          {children}
        </div>
        </body>
      </html>
  );
}
