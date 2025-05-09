"use client";

import { useState, useEffect, ReactNode } from "react";
import DarkButton from "@/components/DarkButton";

interface GenericPopupProps {
  onCloseAction: () => void;
  onConfirmAction?: () => void;
  title: string;
  children: ReactNode;
  confirmText?: string;
  showConfirm?: boolean;
  initiallyVisible?: boolean;
}

export default function GenericPopup({
                                       onCloseAction,
                                       onConfirmAction,
                                       title,
                                       children,
                                       confirmText = "Potwierdź",
                                       showConfirm = true,
                                       initiallyVisible = false,
                                     }: GenericPopupProps) {
  const [isVisible, setIsVisible] = useState(initiallyVisible);

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onCloseAction(), 300);
  };

  const handleConfirm = () => {
    if (onConfirmAction) {
      onConfirmAction();
    } else {
      handleClose();
    }
  };

  useEffect(() => {
    const timeout = setTimeout(() => {
      setIsVisible(true);
    }, 50);

    return () => clearTimeout(timeout);
  }, []);

  return (
      <div
          className={`z-[999] w-full h-screen fixed top-0 left-0 flex justify-center items-center select-none transition-opacity duration-200 ${
              isVisible ? "opacity-100" : "opacity-0"
          }`}
      >
        {/* Background overlay */}
        <div
            className="absolute w-full h-full bg-[#818181] opacity-70 transition-opacity duration-300"
            onClick={handleClose}
        ></div>

        {/* Modal container */}
        <div
            className={`bg-background border-[1px] border-[rgba(38,38,38,0.5)] px-10 py-10 rounded-[10px] z-10 transition-all duration-200 ease-in-out transform ${
                isVisible ? "scale-100 opacity-100" : "scale-90 opacity-0"
            }`}
        >
          <div className="mb-6">
            <h2 className="text-2xl font-medium">{title}</h2>
          </div>

          {/* Popup content */}
          <div className="popup-content">{children}</div>

          {/* Confirmation button (optional) */}
          {showConfirm && (
              <div className="mt-7 w-full">
                <DarkButton
                    onClick={handleConfirm}
                    text={confirmText}
                />
              </div>
          )}
        </div>
      </div>
  );
}