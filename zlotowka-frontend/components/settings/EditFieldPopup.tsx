import GenericPopup from "@/components/general/GenericPopup";
import {EditFieldPopupProps} from "@/interfaces/settings/Settings";
import {useState} from "react";

const inputClass =
    "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md w-full lg:min-w-76 ";

export default function EditFieldPopup({
                                         onCloseAction,
                                         title,
                                         fieldName,
                                         initialValue = "",
                                         onSave,
                                       }: EditFieldPopupProps) {
  const [value, setValue] = useState(initialValue);

  const handleSave = () => {
    onSave(value, fieldName);
    onCloseAction();
  };

  return (
      <GenericPopup onCloseAction={onCloseAction} title={title} onConfirmAction={handleSave} >
        <div className="flex flex-col">
          <div>
            <input
                className={inputClass}
                type="text"
                id={`edit-${fieldName}`}
                value={value}
                onChange={(e) => setValue(e.target.value)}
            />
          </div>
        </div>
      </GenericPopup>
  );
}