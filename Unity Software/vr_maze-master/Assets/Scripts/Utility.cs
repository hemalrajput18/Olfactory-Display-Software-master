using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UnityEngine;

namespace Assets.Scripts
{
    static class Utility
    {

        #region Graphics Utils
        public static class GraphicsUtils {
            /// <summary>
            /// Generates stimulus quadrants
            /// </summary>
            /// <param name="verts">original vertices</param>
            /// <param name="newVertices">modifed vertices</param>
            /// <param name="newUVs">mesh</param>
            /// <param name="newTriangles">created triangles</param>
            public static void AddQuad(List<Vector3> verts, ref List<Vector3> newVertices,
            ref List<Vector2> newUVs, ref List<int> newTriangles)
            {
                var index = newVertices.Count;

                foreach (var vert in verts)
                {
                    newVertices.Add(vert);
                }

                newUVs.Add(new Vector2(1, 0));
                newUVs.Add(new Vector2(1, 1));
                newUVs.Add(new Vector2(0, 1));
                newUVs.Add(new Vector2(0, 0));

                newTriangles.Add(index + 2);
                newTriangles.Add(index + 1);
                newTriangles.Add(index);

                newTriangles.Add(index + 3);
                newTriangles.Add(index + 2);
                newTriangles.Add(index);
            }
        }
        #endregion

        #region JsonHelper
        public static class JsonHelper
        {
            public static T[] FromJson<T>(string json)
            {
                try
                {
                    Wrapper<T> wrapper = JsonUtility.FromJson<Wrapper<T>>(json);
                    return wrapper.Items;
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                    return null;
                }
            }

            public static string ToJson<T>(T[] array)
            {
                Wrapper<T> wrapper = new Wrapper<T>();
                wrapper.Items = array;
                return JsonUtility.ToJson(wrapper);
            }

            public static string ToJson<T>(T[] array, bool prettyPrint)
            {
                Wrapper<T> wrapper = new Wrapper<T>();
                wrapper.Items = array;
                return JsonUtility.ToJson(wrapper, prettyPrint);
            }

            [Serializable]
            private class Wrapper<T>
            {
                public T[] Items;
            }
        }
        #endregion

        #region FloatVariable
        [CreateAssetMenu]
        public class FloatVariable : ScriptableObject
        {
        #if UNITY_EDITOR
            [Multiline]
            public string DeveloperDescription = "";
        #endif
            public float Value;

            public void SetValue(float value)
            {
                Value = value;
            }

            public void SetValue(FloatVariable value)
            {
                Value = value.Value;
            }

            public void ApplyChange(float amount)
            {
                Value += amount;
            }

            public void ApplyChange(FloatVariable amount)
            {
                Value += amount.Value;
            }
        }
        #endregion
    
    }
}
