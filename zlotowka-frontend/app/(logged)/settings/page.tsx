"use client";

import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import DarkButton from "@/components/DarkButton";
import Image from "next/image";
import { JSX, useState } from "react";
import { useUserService } from "@/services/UserService";
import { useQuery } from "@tanstack/react-query";

interface AccountOption {
  text: string;
  avatar?: JSX.Element;
  value?: string;
  onClick?: () => void;
}

interface AccountFieldProps {
  text: string;
  value?: string;
  avatar?: JSX.Element;
  onClick?: () => void;
}

const AccountField = ({
  text,
  value,
  avatar,
  onClick = () => {},
}: AccountFieldProps) => (
  <div className="grid grid-cols-[1fr_80px]  sm:grid-cols-[1fr_1fr_1fr] w-full py-4 border-t-2 border-dashed border-neutral-200 last:border-b-2">
    <div className="flex justify-left sm:row-start-1 w-full  items-center">
      <h3 className="text-md xl:text-lg">{text}</h3>
    </div>
    <div className="flex row-start-2 sm:row-start-1 justify-left xl:justify-center items-center">
      {avatar && <div>{avatar}</div>}
      {value && (
        <span className="text-neutral-600 text-md xl:text-lg font-lato">
          {value}
        </span>
      )}
    </div>
    <div className="flex  justify-end row-start-1 row-end-3 sm:row-end-1 items-center item">
      <div className="w-18 sm:w-28 h-10">
        <DarkButton text={"Edytuj"} onClick={onClick} />
      </div>
    </div>
  </div>
);

const scrollToSection = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: "smooth" });
};

export default function Settings() {
  const navLinks: string[] = ["Konto", "Preferencje", "Powiadomienia"];
  const UserService = useUserService();
  const { data } = useQuery({
    queryKey: ["user", "getUserData"],
    queryFn: UserService.getUserData,
  });

  const [darkMode, setDarkMode] = useState(data?.darkMode || false);
  const [notificationsByEmail, setNotificationsByEmail] = useState(
    data?.notificationsByEmail || false
  );
  const [notificationsByPhone, setNotificationsByPhone] = useState(
    data?.notificationsByPhone || false
  );

  const AccountOptions: AccountOption[] = [
    {
      text: "Zdjęcie",
      avatar: (
        <Image
          src="/avatar.png"
          width={45}
          height={45}
          alt="Avatar"
          className="rounded-full"
        />
      ),
      onClick: () => {},
    },
    {
      text: "Nazwa użytkownika",
      value: data?.firstName
        ? `${data?.firstName} ${data?.lastName}`
        : "Użytkownik...",
      onClick: () => {},
    },
    {
      text: "E-mail",
      value: data?.email ? data?.email : "Email...",
      onClick: () => {},
    },
    {
      text: "Numer telefonu",
      value: data?.phoneNumber ? data?.phoneNumber : "Nie podano...",
      onClick: () => {},
    },
  ];

  return (
    <div className="w-full p-8 min-h-screen xl:px-32 xl:py-20 font-semibold text-accent">
      <div>
        <h1 className="text-4xl">Ustawienia</h1>
      </div>
      <div className="flex flex-col sm:flex-row gap-y-2 sm:gap-x-6 mt-8">
        {navLinks.map((link) => (
          <div
            key={link}
            className="px-6 py-2 bg-neutral-200 rounded-lg hover:cursor-pointer hover:bg-neutral-300 transition-all ease-in-out duration-200"
            onClick={() => scrollToSection(link)}
          >
            <p className="text-lg">{link}</p>
          </div>
        ))}
      </div>

      <GenericCard className="mt-10 max-w-3xl p-6" id="Konto">
        <h2 className="text-2xl xl:text-3xl">Informacje ogólne</h2>
        <div className="mt-4">
          {AccountOptions.map((option, index) => (
            <AccountField
              key={index}
              text={option.text}
              value={option.value}
              avatar={option.avatar}
              onClick={option.onClick}
            />
          ))}
        </div>
      </GenericCard>

      <GenericCard className="mt-10 max-w-3xl p-6" id={"Preferencje"}>
        <h2 className="text-2xl md:text-3xl">Preferencje</h2>
        <div className="mt-4">
          <div className="flex justify-between items-center">
            <h4 className="text-md md:text-lg">Ciemny motyw</h4>
            <div className="w-22 sm:w-28 h-10">
              <DarkButton
                className={`${
                  darkMode
                    ? "bg-accent text-background"
                    : "bg-neutral-400 text-accent"
                }`}
                text={darkMode ? "Włączony" : "Wyłączony"}
                onClick={() => setDarkMode(!darkMode)}
              />
            </div>
          </div>
        </div>
      </GenericCard>

      <GenericCard className="mt-10 max-w-3xl p-6" id={"Powiadomienia"}>
        <h2 className="text-2xl md:text-3xl">Powiadomienia</h2>
        <div className="mt-4">
          <div className="flex justify-between items-center border-t-2 border-dashed border-neutral-200 py-4">
            <h4 className="text-md md:text-lg">Na email</h4>
            <div className="w-22 sm:w-28 h-10">
              <DarkButton
                className={`${
                  notificationsByEmail
                    ? "bg-accent text-background"
                    : "bg-neutral-400 text-accent"
                }`}
                text={notificationsByEmail ? "Włączony" : "Wyłączony"}
                onClick={() => setNotificationsByEmail(!notificationsByEmail)}
              />
            </div>
          </div>
          <div className="flex justify-between items-center border-y-2 border-dashed border-neutral-200 py-4">
            <h4 className="text-md md:text-lg">Na telefon</h4>
            <div className="w-22 sm:w-28 h-10">
              <DarkButton
                className={`${
                  notificationsByPhone
                    ? "bg-accent text-background"
                    : "bg-neutral-400 text-accent"
                }`}
                text={notificationsByPhone ? "Włączony" : "Wyłączony"}
                onClick={() => setNotificationsByPhone(!notificationsByPhone)}
              />
            </div>
          </div>
        </div>
      </GenericCard>
    </div>
  );
}
