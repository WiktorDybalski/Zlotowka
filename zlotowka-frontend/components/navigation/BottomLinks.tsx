import Link from "next/link";

interface BottomLinks{
  icon: string,
  text: string,
  href: string,
}

export default function BottomLinks({ onLinkClick }: { onLinkClick?: () => void }) {
  const links: BottomLinks[] = [
    {
      icon: "settings",
      text: "Ustawienia",
      href: "/settings"
    },
    {
      icon: "logout",
      text: "Wyloguj się",
      href: "/"
    }

  ]

  return (
      <>
      {links.map((link) => (
            <Link href={link.href} onClick={onLinkClick} key={link.href}>
              <div className="flex items-center hover:text-neutral-300 cursor-pointer transition-colors my-5 xl:my-3">
                <span className="material-symbols text-xl font-light">{link.icon}</span>
                <div>
                  <p className="ml-3 text-xl">{link.text}</p>
                </div>
              </div>
            </Link>
      ))}
      </>
  );
}
