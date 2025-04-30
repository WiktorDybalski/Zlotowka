export default function DreamLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <main className="relative w-full h-screen px-6 py-12 lg:px-20 lg:py-20 2xl:px-40">
      {children}
    </main>
  );
}
