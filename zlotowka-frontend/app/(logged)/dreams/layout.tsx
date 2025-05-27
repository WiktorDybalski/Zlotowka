import { DreamProvider } from "@/components/dreams/DreamsContext";

export default function DreamLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <DreamProvider>
      <main className="relative w-full min-h-screen px-6 py-12 lg:px-20 lg:py-20 2xl:px-40">
        {children}
      </main>
    </DreamProvider>
  );
}
