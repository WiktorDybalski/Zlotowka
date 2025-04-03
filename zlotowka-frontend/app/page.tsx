import Image from "next/image";
import Waves from "@/components/main_page/AbsoluteWaves";
import Link from "next/link";
import RegisterButton from "@/components/general/Button";
import LightLink from "@/components/general/LightLink";
import routes from "@/routes";

function getRandomInt(min: number, max: number) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function Home() {
  return (
    <main className="min-h-screen overflow-hidden relative">
      <Waves>
        {/* Coins scattered randomly*/}
        <section>
          {[...Array(20)].map((_, i) => {
            const size = getRandomInt(50, 100);
            const x = getRandomInt(1, 100);
            const y = getRandomInt(1, 100);
            const rotation = getRandomInt(0, 360);
            return (
              <Image
                key={i}
                src="/assets/coin.svg"
                alt="Coin"
                width={size}
                height={size}
                className="absolute"
                style={{
                  top: `${y}vh`,
                  left: `${x}vw`,
                  transform: `rotate(${rotation}deg)`,
                  height: `${size}px`,
                  width: `${size}px`,
                }}
              />
            );
          })}
        </section>
      </Waves>

      {/* Main content */}
      <section className="flex flex-col items-center justify-center min-h-screen px-4 relative ">
        <figure className="p-4 bg-background/70 rounded-lg">
          <div className="text-center mb-10">
            <h1 className="text-4xl md:text-6xl font-bold text-accent mb-2">
              Złotówka
            </h1>
          </div>

          <div className="flex flex-col items-center space-y-4">
            <RegisterButton>
              <Link className="block" href={routes.register.pathname}>
                Zarejestruj się!
              </Link>
            </RegisterButton>
            <LightLink href={routes.login.pathname}>Mam już konto</LightLink>
          </div>
        </figure>
      </section>
    </main>
  );
}
