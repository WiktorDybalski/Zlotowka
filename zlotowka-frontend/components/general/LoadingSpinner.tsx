import React from "react";

interface LoadingSpinnerProps {
  minH?: number;
  size?: number;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  minH = 40,
  size = 16,
}) => (
  <div
    className={`flex justify-center items-center min-h-${minH} my-auto h-full w-full select-none`}
  >
    <div
      className={`border-t-4 border-accent border-solid w-${size} h-${size} rounded-full animate-spin`}
    ></div>
  </div>
);

export default LoadingSpinner;
