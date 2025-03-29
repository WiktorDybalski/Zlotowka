import GenericCard from "@/components/cards/GenericCard";
import {ReactNode} from "react";

interface ThreeElementsCardProps {
  className?: string;
  top: ReactNode;
  middle: ReactNode;
  bottom: ReactNode;
}


export default function ThreeElementsCard({ top, middle, bottom, className }: ThreeElementsCardProps) {
  return (
      <GenericCard>
          <div className="w-full h-full px-7 py-5 flex flex-col justify-center font-semibold text-neutral-800">
            <div className="w-full text-xl">
              {top}
            </div>
            <div className="w-full text-4xl font-lato my-auto">
              {middle}
            </div>
            <div className="w-full text-lg">
              {bottom}
            </div>
          </div>
      </GenericCard>
  )
}