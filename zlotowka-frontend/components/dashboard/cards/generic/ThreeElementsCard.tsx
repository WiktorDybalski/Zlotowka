import { ReactNode } from "react";

interface ThreeElementsCardProps {
  top?: ReactNode;
  middle?: ReactNode;
  bottom?: ReactNode;
}

export default function ThreeElementsCard({
  top,
  middle,
  bottom,
}: ThreeElementsCardProps) {
  return (
    <div className="w-full h-full px-7 py-5 flex flex-col justify-center text-accent">
      {top && (
        <div className="w-full text-xl lg:text-lg 2xl:text-xl">{top}</div>
      )}
      {middle && (
        <div className="w-full text-3xl lg:text-2xl xl:text-3xl 2xl:text-4xl font-lato my-auto py-4">
          {middle}
        </div>
      )}
      {bottom && (
        <div className="w-full text-[clamp(0.8rem,1vw,1.20rem)]">{bottom}</div>
      )}
    </div>
  );
}
