"use client";

import { useQueryWithToast } from "@/lib/data-grabbers";
import { useUserService } from "@/services/UserService";
import Image from "next/image";
import React, { useState } from "react";
import NotificationsPanel from "@/components/notifications/NotificationsPanel";

export default function UserInfo() {
  const UserService = useUserService();
  const { data } = useQueryWithToast({
    queryKey: ["user"],
    queryFn: UserService.fetchUserData,
  });

  const dispalyedName =
      data?.firstName && data?.lastName
          ? `${data.firstName} ${data.lastName}`
          : "Użytkownik...";
  const displayedEmail = data?.email ? data.email : "Email...";

  const [showNotifications, setShowNotifications] = useState(false);

  const toggleNotifications = () => {
    setShowNotifications((prev) => !prev);
  };

  return (
      <div className="flex ml-4 xl:ml-0 items-center justify-between w-full">
        <div className="flex items-center">
          <div className="hidden xl:block mr-3">
            <Image
                src="/avatar.png"
                width={65}
                height={65}
                alt="Avatar"
                className="rounded-full"
            />
          </div>
          <div>
            <h3 className="text-xl">{dispalyedName}</h3>
            <h4 className="hidden xl:block text-neutral-400 text-xs">
              {displayedEmail}
            </h4>
          </div>
        </div>

        <div className="flex items-center ml-3 group hover:cursor-pointer">
        <span
            className="material-symbols text-background transition-colors duration-200 group-hover:text-yellow-500"
            onClick={toggleNotifications}
            aria-label="Pokaż powiadomienia"
        >
          notifications
        </span>
        </div>

        {showNotifications && (
            <NotificationsPanel onClose={() => setShowNotifications(false)} />
        )}
      </div>
  );
}
