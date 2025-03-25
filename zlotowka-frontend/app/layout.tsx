import localFont from 'next/font/local'
import "./globals.css";

import { Lato, Raleway } from 'next/font/google';
const lato = Lato({
  weight: ['100', '300', '400', '700', '900'],
  style: ['normal', 'italic'],
  subsets: ['latin', 'latin-ext'],
  display: 'swap',
});

const raleway = Raleway({
  weight: ['100', '500', '600', '900'],
  style: ['normal', 'italic'],
  subsets: ['latin', 'latin-ext'],
  display: 'swap',
});

const materialSymbols = localFont({
  variable: '--font-family-symbols', // Custom CSS variable
  style: 'normal',
  src: '../node_modules/material-symbols/material-symbols-rounded.woff2',
  display: 'block',
  weight: '100 700', // Zakres wag
});



export default function RootLayout({
                                     children,
                                   }: {
  children: React.ReactNode;
}) {
  return (
      <html lang="pl">
      <body className={`${lato.className} ${raleway.className} ${materialSymbols.variable}`}>
        {children}
      </body>
      </html>
  );
}