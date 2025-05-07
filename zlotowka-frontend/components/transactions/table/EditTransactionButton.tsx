"use client";

import React from "react";
import clsx from "clsx";

interface EditTransactionButtonProps {
  onClick: () => void;
  className?: string;
}

export default function EditTransactionButton({
  onClick,
  className,
}: EditTransactionButtonProps) {
  return (
    <button
      onClick={onClick}
      className={clsx(
        "flex items-center justify-center rounded-lg bg-gray-700 p-2 hover:bg-gray-600 transition",
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
        edit
      </span>
    </button>
  );
}
