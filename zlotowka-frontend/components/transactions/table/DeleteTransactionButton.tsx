"use client";

import React from "react";
import clsx from "clsx";

interface DeleteTransactionButtonProps {
  onClick: () => void;
  className?: string;
}

export default function DeleteTransactionButton({
  onClick,
  className,
}: DeleteTransactionButtonProps) {
  return (
    <button
      onClick={onClick}
      className={clsx(
        "flex items-center justify-center rounded-lg bg-red-700 p-2 hover:bg-red-600 transition",
        className
      )}
    >
      <span
        className="material-symbols text-white"
        style={{
          fontSize: "1.1rem",
          lineHeight: "1",
        }}
      >
        delete
      </span>
    </button>
  );
}
