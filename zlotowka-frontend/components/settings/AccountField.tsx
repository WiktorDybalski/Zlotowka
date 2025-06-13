import React from "react";
import DarkButton from "@/components/DarkButton";
import {AccountFieldProps} from "@/interfaces/settings/Settings";

export const AccountField: React.FC<AccountFieldProps> = ({ text, value,placeholder, avatar, onClick}) => (
    <div className="grid grid-cols-[1fr_80px] sm:grid-cols-[1fr_1fr_1fr] w-full py-4 border-t-2 border-dashed border-neutral-200 first:border-none ">
      <div className="flex justify-left sm:row-start-1 w-full items-center">
        <h3 className="text-md xl:text-lg">{text}</h3>
      </div>
        <div className="flex row-start-2 sm:row-start-1 justify-left xl:justify-center items-center">
          {avatar && <div>{avatar}</div>}
            <span
              className={
                (value
                  ? "text-neutral-600 dark:text-lightAccent"
                    : "text-neutral-400 italic") +
                  " text-md xl:text-lg font-lato"
                }
            >
        {value ?? placeholder}
      </span>
        </div>
        {onClick && (
          <div className="flex justify-end row-start-1 row-end-3 sm:row-end-1 items-center">
            <div className="w-18 sm:w-28 h-10">
              <DarkButton text="Edytuj" onClick={onClick}/>
            </div>
          </div>
        )}
    </div>
);