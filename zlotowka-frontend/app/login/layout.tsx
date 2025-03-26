import Waves from "@/components/main_page/AbsoluteWaves";

export default function LoginLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <main className="min-h-screen overflow-hidden bg-white relative">
      <Waves />
      <section className="flex flex-col items-center justify-center min-h-screen px-4 relative ">
        {children}
      </section>
    </main>
  );
}
