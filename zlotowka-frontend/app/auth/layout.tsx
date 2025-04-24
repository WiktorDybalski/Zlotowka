import ReturnButton from "@/components/general/ReturnButton";
import Waves from "@/components/main_page/AbsoluteWaves";
import { RedirectWhenLogged } from "@/components/providers/AuthProvider";
import routes from "@/routes";

export default function LoginLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <RedirectWhenLogged>
      <main className="min-h-screen overflow-hidden bg-white relative">
        <Waves />
        <section className="flex flex-col items-center justify-center min-h-screen px-4 relative ">
          <ReturnButton href={routes.heropage.pathname} />
          {children}
        </section>
      </main>
    </RedirectWhenLogged>
  );
}
