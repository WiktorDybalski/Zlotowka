import {ReactNode} from "react";

interface GenericCardProps {
  children: ReactNode;
  className?: string;
}

// TODO: fix opacity
export default function GenericCard({ children, className }: GenericCardProps) {
  return (
      <div className={`border-[1px] rounded-xl ${className}`} style={{ borderColor: "rgba(38, 38, 38, 0.5)" }}>
        {children}
      </div>
  );
}
