import localFont from "next/font/local";
import { Raleway, Lato } from "next/font/google";
import "./globals.css";

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

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pl">
      <body
        className={`${raleway.variable} ${lato.variable} ${materialSymbols.variable} font-(family-name:--font-raleway) bg-background`}
      >
        {children}
      </body>
    </html>
  );
}
