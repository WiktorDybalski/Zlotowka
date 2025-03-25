import "./globals.css";
import Navigation from "@/components/navigation/Navigation";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pl">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" />

        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL,GRAD@100..700,0..1,-50..200"
        />

        <link
          href="https://fonts.googleapis.com/css2?family=Lato:ital,wght@0,100;0,300;0,400;0,700;0,900;1,100;1,300;1,400;1,700;1,900&display=optional&family=Raleway:ital,wght@0,100..900;1,100..900&display=optional"
          rel="stylesheet"
        />
      </head>
      <body className="flex flex-col min-h-screen lg:flex-row">
        <div className="w-full min-h-20 left-0 top-0 font-medium text-neutral-100 lg:w-96 lg:h-screen lg:sticky">
          <Navigation />
        </div>
        <div className="w-full">{children}</div>
      </body>
    </html>
  );
}
