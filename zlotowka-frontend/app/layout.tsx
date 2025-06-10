import localFont from "next/font/local";
import { Raleway, Lato } from "next/font/google";
import ReactQuerryProvider from "@/components/providers/react-querry";
import "./globals.css";
import { AuthProvider } from "@/components/providers/AuthProvider";
import { Toaster } from "react-hot-toast";
import { Metadata } from "next";

const raleway = Raleway({
  weight: ["400", "500", "600", "700"],
  subsets: ["latin"],
  display: "swap",
  variable: "--font-raleway",
});

const lato = Lato({
  subsets: ["latin"],
  display: "swap",
  weight: ["400", "700"],
  variable: "--font-lato",
});

const materialSymbols = localFont({
  variable: "--font-family-symbols",
  style: "normal",
  src: "../node_modules/material-symbols/material-symbols-rounded.woff2",
  display: "block",
  weight: "100 700",
});

const materialSymbolsOutlined = localFont({
  variable: "--font-family-symbols-outlined",
  style: "normal",
  src: "../node_modules/material-symbols/material-symbols-outlined.woff2",
  display: "block",
  weight: "100 700",
});

export const metadata: Metadata = {
  title: "Złotówka",
  description: "Zlotowka",
};

export default function RootLayout({
                                     children,
                                   }: {
  children: React.ReactNode;
}) {
  return (
      <html lang="pl" className="custom-scroll">
      <body
          className={`
          ${raleway.variable} ${lato.variable}
          ${materialSymbols.variable} ${materialSymbolsOutlined.variable}
          font-(family-name:--font-raleway) bg-background
        `}
      >
      <Toaster position="top-center" reverseOrder={false} />

      <div id="notifications-root" />

      <ReactQuerryProvider>
        <AuthProvider>{children}</AuthProvider>
      </ReactQuerryProvider>
      </body>
      </html>
  );
}
