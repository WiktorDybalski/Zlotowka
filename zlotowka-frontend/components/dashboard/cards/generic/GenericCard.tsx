import {ReactNode} from "react";

interface GenericCardProps {
  children: ReactNode;
  className?: string;
  id?: string;
}

export default function GenericCard({ children, className, id }: GenericCardProps) {
  return (
      <div className={`w-full h-full border-[1px] rounded-xl ${className}`} id={id} style={{ borderColor: "rgba(38, 38, 38, 0.5)" }}>
        {children}
      </div>
  );
}
