using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Hotspot.Manager.RNHotspotManager
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNHotspotManagerModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNHotspotManagerModule"/>.
        /// </summary>
        internal RNHotspotManagerModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNHotspotManager";
            }
        }
    }
}
