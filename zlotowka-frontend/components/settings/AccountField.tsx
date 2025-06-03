import DarkButton from "@/components/DarkButton";
import {AccountFieldProps} from "@/interfaces/settings/Settings";

export const AccountField: React.FC<AccountFieldProps> = ({ text, value, avatar, onClick = () => {} }) => (
    <div className="grid grid-cols-[1fr_80px] sm:grid-cols-[1fr_1fr_1fr] w-full py-4 border-t-2 border-dashed border-neutral-200 last:border-b-2">
      <div className="flex justify-left sm:row-start-1 w-full items-center">
        <h3 className="text-md xl:text-lg">{text}</h3>
      </div>
      <div className="flex row-start-2 sm:row-start-1 justify-left xl:justify-center items-center">
        {avatar && <div>{avatar}</div>}
        {value && (
            <span className="text-neutral-600 text-md xl:text-lg font-lato">
          {value}
        </span>
        )}
      </div>
      <div className="flex justify-end row-start-1 row-end-3 sm:row-end-1 items-center">
        <div className="w-18 sm:w-28 h-10">
          <DarkButton text="Edytuj" onClick={onClick} />
        </div>
      </div>
    </div>
);