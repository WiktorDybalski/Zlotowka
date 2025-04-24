import { motion, AnimatePresence } from "framer-motion";
import NavigationLinks from "@/components/navigation/NavigationLinks";
import BottomLinks from "@/components/navigation/BottomLinks";
import { MenuProps } from "@/interfaces/navigation/MenuProps";
import Divider from "@/components/navigation/Divider";

export default function MobileMenu({ isOpen, navLinks, onLinkClick }: MenuProps) {
  const mobileContentVariants = {
    hidden: {
      opacity: 0,
      x: -50,
      transition: {
        duration: 0.3,
      },
    },
    visible: {
      opacity: 1,
      x: 0,
      transition: {
        duration: 0.3,
        delay: 0.2,
      },
    },
  };

  return (
      <AnimatePresence>
        {isOpen && (
            <motion.div
                className="xl:hidden w-full h-full flex flex-col overflow-hidden"
                variants={mobileContentVariants}
                initial="hidden"
                animate="visible"
                exit="hidden"
            >
              <Divider />
              <NavigationLinks links={navLinks} isMobile={true} onLinkClick={onLinkClick} />
              <Divider />
              <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{
                    opacity: 1,
                    y: 0,
                    transition: { delay: 0.6 },
                  }}
              >
                <BottomLinks onLinkClick={onLinkClick} />
              </motion.div>
            </motion.div>
        )}
      </AnimatePresence>
  );
}
