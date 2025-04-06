import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import {ReactNode} from "react";

interface ThreeElementsCardProps {
  top: ReactNode;
  middle: ReactNode;
  bottom: ReactNode;
}


export default function ThreeElementsCard({ top, middle, bottom }: ThreeElementsCardProps) {
  return (
      <GenericCard>
          <div className="w-full h-full px-7 py-5 flex flex-col justify-center text-neutral-800">
            <div className="w-full text-xl lg:text-lg 2xl:text-xl">
              {top}
            </div>
            <div className="w-full text-3xl lg:text-2xl xl:text-3xl 2xl:text-4xl font-lato my-auto py-4">
              {middle}
            </div>
            <div className="w-full text-lg lg:text-lg 2xl:text-xl">
              {bottom}
            </div>
          </div>
      </GenericCard>
  )
}