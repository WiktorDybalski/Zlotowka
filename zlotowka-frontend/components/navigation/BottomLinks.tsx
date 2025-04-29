"use client";

import { useAuth } from "../providers/AuthProvider";
import routes from "@/routes";
import React from "react";
import { redirect } from "next/navigation";
import toast from "react-hot-toast";

interface BottomLink {
  icon: string;
  text: string;
  href: string;
  onClick: (e: React.MouseEvent<HTMLDivElement>) => void;
}

interface BottomLinksProps {
  onLinkClick?: () => void;
}

export default function BottomLinks({
  onLinkClick = () => {},
}: BottomLinksProps) {
  const Auth = useAuth();

  const links: BottomLink[] = [
    {
      icon: "settings",
      text: "Ustawienia",
      href: routes.settings.pathname,
      onClick: () => {
        onLinkClick();
        redirect(routes.settings.pathname);
      },
    },
    {
      icon: "logout",
      text: "Wyloguj się",
      href: routes.heropage.pathname,
      onClick: () => {
        onLinkClick();
        Auth.setLogout();
        toast.success("Wylogowano pomyślnie!");
        // redirect(routes.heropage.pathname); //auto redirect
      },
    },
  ];

  return (
    <>
      {links.map((link) => (
        <div
          key={link.href}
          onClick={(e) => link.onClick(e)}
          className="flex items-center hover:text-neutral-300 cursor-pointer transition-colors my-5 xl:my-3"
        >
          <span className="material-symbols text-xl font-light">
            {link.icon}
          </span>
          <div>
            <p className="ml-3 text-xl">{link.text}</p>
          </div>
        </div>
      ))}
    </>
  );
}
